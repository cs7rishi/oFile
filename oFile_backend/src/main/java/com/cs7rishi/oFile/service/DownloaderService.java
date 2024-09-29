package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.dto.FileDto;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DownloaderService {
    boolean downloadFile(FileDto fileDto) throws IOException, URISyntaxException;

    boolean deleteFile(String fileId);
}
