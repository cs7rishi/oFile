package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.constants.ResponseConstant;
import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.service.FileService;
import com.cs7rishi.oFile.utils.ApiResponseUtil;
import com.cs7rishi.oFile.utils.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    DownloaderService downloaderService;

    /**
     * This method creates the new file, add it in the DB
     * and send the file detail to downloadService to download the file
     * @param fileDto fileDetails
     * @return Success or Failure Response
     */
    @Override
    public GenericResponse<?> add(FileDto fileDto){
        fileRepository.save(createFileEntityForAuthorisedUser(fileDto));
        //Todo
        //downloaderService.downloadFile(fileDto);
        return ApiResponseUtil.success(null, ResponseConstant.FILE_ADD_SUCCESS, ResponseConstant.EMPTY);
    }

    @Override
    public GenericResponse<?> delete(String fileId) {
        try {
            fileRepository.deleteById(Long.parseLong(fileId));
            downloaderService.deleteFile(fileId);
        } catch (Exception ex) {
            System.out.println("Error while delete file from DB");
        }
        return ApiResponseUtil.success(null, ResponseConstant.FILE_DELETE_SUCCESS, ResponseConstant.EMPTY);
    }

    @Override
    public GenericResponse<?> list() {
        List<FileEntity> files = extractFileForAuthorisedUser();
        return ApiResponseUtil.success(files,ResponseConstant.EMPTY,ResponseConstant.EMPTY);
    }

    @Override
    public SseEmitter status() {
        FileDto fileDto1 =
            FileDto.builder().id(1L).fileName("Report.pdf").progress(60).progress(0)
                .fileSize(560).downloadedSize(0).build();
        FileDto fileDto2 =
            FileDto.builder().id(2L).fileName("Image.png").progress(20).progress(0)
                .fileSize(230).downloadedSize(0).build();

        ArrayList<FileDto> fileList = new ArrayList<>();
        fileList.add(fileDto1);
        fileList.add(fileDto2);
        SseEmitter emitter = new SseEmitter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    SseEmitter.SseEventBuilder event =
                        SseEmitter.event().data(updateFiles(fileList))
                            .id(String.valueOf(i)).name("message");
                    emitter.send(event);
                    Thread.sleep(3000);
                    if(i == 10) break;
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    private FileEntity createFileEntityForAuthorisedUser(FileDto fileDto){
        String email = getAuthorisedUser().getEmail();
        Customer customer = customerRepository.findByEmail(email).get(0);
        return FileDto.createEntity(fileDto,email,customer);
    }

    private List<FileEntity> extractFileForAuthorisedUser(){
        return fileRepository.findByCustomer(getAuthorisedUser());
    }

    private Customer getAuthorisedUser(){
        String email = AuthorizationUtils.getUserEmail();
        return customerRepository.findByEmail(email).get(0);
    }

    private List<FileDto> updateFiles(ArrayList<FileDto> fileList) {
        fileList.forEach(file -> {
            file.setDownloadedSize(Math.min(file.getFileSize(),
                generateRandomIntegerInRange(file.getDownloadedSize(), file.getFileSize())));
            file.setProgress(calculateProgress(file.getDownloadedSize(),file.getFileSize()));
        });

        return fileList;
    }

    private int calculateProgress(int downloadedData,int totalData){
        if (totalData == 0) {
            return 0; // Avoid division by zero
        }

        double progressPercentage = (downloadedData * 100.0) / totalData;
        return (int) progressPercentage;
    }

    private Integer generateRandomIntegerInRange(int min, int max) {
        int newMax = Math.min(min+5, max);
        Random random = new Random();
        return random.nextInt(newMax) + min;
    }


}
