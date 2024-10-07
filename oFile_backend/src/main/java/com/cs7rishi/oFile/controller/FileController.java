package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.model.request.StreamRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileService fileService;

    @PostMapping("/add")
    public GenericResponse<?> add(@RequestBody FileDto fileDto) throws OFileException {
        return fileService.add(fileDto);
    }

    @GetMapping("/delete")
    public GenericResponse<?> delete(@RequestParam("fileId") Long fileId) {
        return fileService.delete(fileId);
    }

    @GetMapping("/list")
    public GenericResponse<?> list(){
        return fileService.list();
    }

    @GetMapping("/stream")
    public SseEmitter status(@RequestParam List<Long> ids) {
        return fileService.stream(new StreamRequest(ids));
    }
}
