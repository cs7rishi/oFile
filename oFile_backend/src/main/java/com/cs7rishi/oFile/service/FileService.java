package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.model.response.GenericResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface FileService {
    GenericResponse<?> add(FileDto fileDto) throws IOException, URISyntaxException;
    GenericResponse<?> delete(String fileId);
    GenericResponse<?> list();

}
