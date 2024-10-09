package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.constants.ResponseConstant;
import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.model.request.StreamRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.model.response.StreamResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.service.FileService;
import com.cs7rishi.oFile.utils.ApiResponseUtil;
import com.cs7rishi.oFile.utils.AuthorizationUtils;
import com.cs7rishi.oFile.utils.FileUtils;
import com.cs7rishi.oFile.utils.S3Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cs7rishi.oFile.utils.FileUtils.calculatePercentage;
import static com.cs7rishi.oFile.utils.FileUtils.convertMBtoBytes;


@Service
public class FileServiceImpl implements FileService {
    @Value("${oFile.fileSizeLimit}")
    private long fileSizeLimitMB;
    @Value("${oFile.s3.bucketName}")
    private String bucketName;
    @Value("${oFile.s3.urlExpirationHour}")
    private Integer urlExpirationHours;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DownloaderService downloaderService;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    DownloadProgressCacheService progressCacheService;
    @Autowired
    ModelMapper modelMapper;

    /**
     * This method creates the new file, add it in the DB
     * and send the file detail to downloadService to download the file
     * @param fileDto fileDetails
     * @return Success or Failure Response
     */
    @Override
    public GenericResponse<?> add(FileDto fileDto) throws OFileException {
        validateAndPersistFile(fileDto);
        downloaderService.downloadFile(fileDto);
        return ApiResponseUtil.success(fileDto, ResponseConstant.FILE_ADD_SUCCESS,
            ResponseConstant.EMPTY);
    }

    @Override
    public GenericResponse<?> delete(Long fileId) {
        try {
            //Todo add customer validation
            fileRepository.deleteById(fileId);
            downloaderService.deleteFile(fileId);
        } catch (Exception ex) {
            System.out.println("Error while delete file from DB");
        }
        return ApiResponseUtil.success(null, ResponseConstant.FILE_DELETE_SUCCESS, ResponseConstant.EMPTY);
    }

    @Override
    public GenericResponse<?> list() {
        List<FileEntity> files = getFileForAuthenticatedUser();
        return ApiResponseUtil.success(files,ResponseConstant.EMPTY,ResponseConstant.EMPTY);
    }

    @Override
    public SseEmitter stream(StreamRequest streamRequest) {
        SseEmitter emitter = new SseEmitter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    StreamResponse streamResponse = createStreamResponse(streamRequest);
                    if(streamResponse.getFiles().isEmpty()){
                        break;
                    }

                    SseEmitter.SseEventBuilder event =
                        SseEmitter.event().data(streamResponse)
                            .id(String.valueOf(i)).name("message");
                    emitter.send(event);
                    Thread.sleep(3000);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @Override
    public GenericResponse<?> downloadFile(Long fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).get();
        return ApiResponseUtil.success(
            S3Utils.createPresignedGetUrl(bucketName, FileUtils.createS3Key(fileId),
                urlExpirationHours, fileEntity.getFileName()), ResponseConstant.EMPTY,
            ResponseConstant.EMPTY);
    }

    private StreamResponse createStreamResponse(StreamRequest streamRequest){
        StreamResponse streamResponse = new StreamResponse();
        List<FileDto> fileDtoList = new ArrayList<>();
        streamRequest.getIds().forEach((fileId) -> {

            int progress = progressCacheService.getFileProgress(fileId);
            if (progress == 100) {
                progressCacheService.deleteFileProgress(fileId);
            }
            if (progress != -1) {
                FileDto fileDto = FileDto.builder().id(fileId).progress(progress).build();
                fileDtoList.add(fileDto);
            }
        });
        streamResponse.setFiles(fileDtoList);
        return streamResponse;
    }

    private void validateAndPersistFile(FileDto fileDto) throws OFileException {
        validateFile(fileDto);
        createAndSaveFile(fileDto);
    }

    private void validateFile(FileDto fileDto) throws OFileException {
        if(!isFileSizeValid(fileDto)){
            throw new OFileException("File is to Big");
        }
    }

    private void createAndSaveFile(FileDto fileDto){
        FileEntity fileEntity = createFileEntity(fileDto);
        fileEntity = fileRepository.save(fileEntity);
        //Todo add a model mapper here
        fileDto.setId(fileEntity.getId());
        fileDto.setProgress(fileEntity.getProgress());
    }

    private boolean isFileSizeValid(FileDto fileDto) throws OFileException {
        long fileSize = FileUtils.getFileSize(fileDto);
        return fileSize <= convertMBtoBytes(fileSizeLimitMB);
    }

    private FileEntity createFileEntity(FileDto fileDto) {
        Customer customer = getAuthorisedCustomer();
        return FileDto.createEntity(fileDto, customer);
    }

    private List<FileEntity> getFileForAuthenticatedUser(){
        return fileRepository.findByCustomer(getAuthorisedCustomer());
    }

    private Customer getAuthorisedCustomer(){
        String email = AuthorizationUtils.getUserEmail();
        return customerRepository.findByEmail(email).get(0);
    }
}
