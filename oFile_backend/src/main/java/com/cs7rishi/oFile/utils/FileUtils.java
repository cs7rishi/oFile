package com.cs7rishi.oFile.utils;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {
    private static final String DOWNLOAD_DIR = "D:\\downloads";
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


    public static String createS3Key(Long fileId){
        return AuthorizationUtils.getUserEmail() + "/" + fileId;
    }

    public static File createFilePath(FileDto fileDto) throws OFileException {
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

    public static File getUserDirectory(){
        File file = new File(DOWNLOAD_DIR, AuthorizationUtils.getUserEmail());
        file.mkdir();
        return file;
    }
}
