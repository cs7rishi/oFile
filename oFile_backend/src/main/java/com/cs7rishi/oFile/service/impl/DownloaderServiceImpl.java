package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.utils.AuthorizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.cs7rishi.oFile.utils.FileUtils.calculatePercentage;

/**
 * This service is responsible for downloading the file in local directory.
 */
@Service
public class DownloaderServiceImpl implements DownloaderService {
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private DownloadProgressCacheService progressCacheService;
    private final String DOWNLOAD_DIR = "D:\\downloads";
    private final int BUFFER_SIZE = 1024;


    @PostConstruct
    private void createDownloadDirectory() {
        File file = new File(DOWNLOAD_DIR);
        System.out.println("Download directory created : " + file.mkdirs());
    }

    @Override
    public void downloadFile(FileDto fileDto) throws OFileException {
        File file = createFilePath(fileDto);
        progressCacheService.initiateProgress(fileDto.getId());
        executorService.submit(() -> {
            startDownload(fileDto, file);
        });
    }

    @Override
    public void deleteFile(Long fileId) {
        //Todo handle the interuption of thread, downloading this file
        progressCacheService.deleteFileProgress(fileId);

    }

    private void startDownload(FileDto fileDto, File file){
        System.out.println(Thread.currentThread().getName());
        try{
            ReadableByteChannel readChannel = Channels.newChannel(generateUrl(fileDto).openStream());
            FileOutputStream outputStream = new FileOutputStream(file);
            FileChannel writeChannel = outputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            long totalBytesRead = 0;

            while(readChannel.read(buffer) != -1){
                totalBytesRead+= buffer.position();
                updateDownloadProgress(fileDto,totalBytesRead);

                buffer.flip();
                writeChannel.write(buffer);
                buffer.clear();

                Thread.sleep(100);
            }
            persistProgressInDB(fileDto);
            outputStream.close();
            readChannel.close();
            writeChannel.close();
            System.out.println("FileId: " + fileDto.getId() + " Download Completed ");
        } catch (Exception ex) {
            System.out.println("Exception Occurred: " + ex.getMessage());
        }
    }

    private void persistProgressInDB(FileDto fileDto) {
        Optional<FileEntity> fileEntity = fileRepository.findById(fileDto.getId());
        fileEntity.ifPresent((entity) -> {
            entity.setProgress(100);
            fileRepository.save(entity);
        });
    }

    private URL generateUrl(FileDto fileModel) throws MalformedURLException{
        return new URL(fileModel.getFileUrl());
    }

    private File createFilePath(FileDto fileDto) throws OFileException {
        File filePath = null;
        try {
            filePath = new File(getUserDirectory(), String.valueOf(fileDto.getId()));
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (Exception ex) {
            throw new OFileException("Unable to create new file");
        }
        return filePath;
    }

    private File getUserDirectory(){
        File file = new File(DOWNLOAD_DIR, AuthorizationUtils.getUserEmail());
        file.mkdir();
        return file;
    }

    private void updateDownloadProgress(FileDto fileDto, long bytesRead) {
        progressCacheService.setFileProgress(fileDto.getId(),
            calculatePercentage(bytesRead, fileDto.getFileSize()));
    }

}
