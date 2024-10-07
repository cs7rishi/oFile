package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DownloaderService {
    void downloadFile(FileDto fileDto) throws OFileException;
    void deleteFile(Long fileId);
}
