package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/add")
    public void add(@RequestBody FileDto fileDto) throws IOException, URISyntaxException {
        fileService.add(fileDto);
    }

    @GetMapping("/delete")
    public void delete(@RequestParam("fileId") String fileId) {
        fileService.delete(fileId);
    }

    @GetMapping("/download")
    public void download() {
        fileService.download();
    }

    @GetMapping("/transfer")
    public void transfer() {
        fileService.transfer();
    }
}
