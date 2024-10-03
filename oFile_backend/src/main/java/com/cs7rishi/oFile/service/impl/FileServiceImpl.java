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

import java.util.List;

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


}
