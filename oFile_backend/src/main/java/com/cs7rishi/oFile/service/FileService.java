package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.dto.FileDto;

import java.io.IOException;
import java.net.URISyntaxException;

public interface FileService {
    boolean add(FileDto fileDto) throws IOException, URISyntaxException;
    void delete(String fileId);
    void download();
    void transfer();
}
