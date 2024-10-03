package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.FileService;
import com.cs7rishi.oFile.utils.AuthorizationUtils;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public GenericResponse<?> add(@RequestBody FileDto fileDto) throws IOException, URISyntaxException {
        return fileService.add(fileDto);
    }

    @GetMapping("/delete")
    public GenericResponse<?> delete(@RequestParam("fileId") String fileId) {
        return fileService.delete(fileId);
    }

    @GetMapping("/list")
    public GenericResponse<?> list(){
        return fileService.list();
    }

    @GetMapping("/status")
    public SseEmitter status(@RequestParam(value = "email" , required = false) String email) {
        return fileService.status();
    }
}
