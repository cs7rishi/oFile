package com.cs7rishi.oFile.utils;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {

    public static long convertMBtoBytes(long mb) {
        return mb * 1024 * 1024;
    }

    private static void updateFileDtoSize(FileDto fileDto, long fileSize){
        fileDto.setFileSize(fileSize);
    }

    public static long getFileSize(FileDto fileDto) throws OFileException {
        try {
            URL url = new URL(fileDto.getFileUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                updateFileDtoSize(fileDto, connection.getContentLengthLong());
                return connection.getContentLengthLong();
            } else {
                throw new OFileException("Failed to get file size");
            }
        } catch (Exception ex) {
            throw new OFileException("Failed to get file size");
        }
    }

    public static int calculatePercentage(long current, long total){
        return (int)((double) current / total * 100);
    }
}
