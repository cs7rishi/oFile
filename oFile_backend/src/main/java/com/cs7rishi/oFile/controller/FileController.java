package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.model.request.StreamRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.FileService;
import com.cs7rishi.oFile.utils.S3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
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

    @Value("${oFile.s3.bucketName}")
    private String bucketName;

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

    @GetMapping("/download")
    public String getDownloadLink(@RequestParam String key){
        return S3Utils.createPresignedGetUrl(bucketName,key);
    }

    @GetMapping("/upload")
    public String uploadtoS3(){
        S3Client s3Client = S3Client.builder().build();
        String bucket_name = "ofile.cs7rishi.me";
        String file_path = "D:\\downloads\\cs7rishi@gmail.com\\1";
        System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket_name)
                .key(file_path)
                .build();

        software.amazon.awssdk.core.sync.RequestBody requestBody = software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(file_path));

        s3Client.putObject(putObjectRequest, requestBody);
        return "success";
    }
}
