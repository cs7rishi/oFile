package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.model.request.StreamRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface FileService {
    GenericResponse<FileDto> add(FileDto fileDto) throws OFileException;
    GenericResponse<?> delete(Long fileId);
    GenericResponse<List<FileDto>> list();
    SseEmitter stream(StreamRequest streamRequest);
    GenericResponse<String> downloadFile(Long fileId);
}
