package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FileRepository fileRepository;
    @Autowired
    DownloaderService downloaderService;

    @Override
    public boolean add(FileDto fileDto) throws IOException, URISyntaxException {
        //Todo Add file against User
        //Todo Download file
        downloaderService.downloadFile(fileDto);
        return true;
    }

    @Override
    public void delete(String fileId) {
        try {
            fileRepository.deleteById(Long.parseLong(fileId));
            downloaderService.deleteFile(fileId);
        } catch (Exception ex) {
            System.out.println("Error while delete file from DB");
        }
    }

    @Override
    public void transfer() {

    }
}
