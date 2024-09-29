package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.service.DownloaderService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * This service is responsible for downloading the file in local directory.
 */
@Service
public class DownloaderServiceImpl implements DownloaderService {
    private final String DIR_NAME = "D:\\downloads";

    @PostConstruct
    private void createDownloadDirectory() {
        File file = new File(DIR_NAME);
        System.out.println("Download directory created : " + file.mkdirs());
    }

    @Override
    public boolean downloadFile(FileDto fileDto) throws IOException, URISyntaxException {
        long fileSize = getFileSize(fileDto);
        ReadableByteChannel readChannel = Channels.newChannel(generateUrl(fileDto).openStream());
        FileOutputStream outputStream = new FileOutputStream(getFilePath(fileDto));
        FileChannel writeChannel = outputStream.getChannel();

        writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
        outputStream.close();
        System.out.println("Download Completed");
        return true;
    }

    @Override
    public boolean deleteFile(String fileId) {
        return false;
    }

    private long getFileSize(FileDto fileDto) throws IOException {
        try {
            URL url = new URL(fileDto.getFileUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getContentLengthLong();
            } else {
                throw new IOException(
                    "Failed to get file size. HTTP response code " + connection.getResponseCode());
            }
        } catch (MalformedURLException ex) {
            System.out.println("Exception occured");
        }

        return -1;
    }

    private URL generateUrl(FileDto fileModel) throws MalformedURLException, URISyntaxException {
        return new URL(fileModel.getFileUrl());
    }

    private File getFilePath(FileDto fileDto) {
        return new File(DIR_NAME, fileDto.getFileName());
    }
}
