package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
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
    FileService fileService;

    @PostMapping("/add")
    public String add(@RequestBody FileDto fileDto) throws IOException, URISyntaxException {
        fileService.add(fileDto);
        return "File added successfully";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("fileId") String fileId) {
        fileService.delete(fileId);
        return "File deleted successfully";
    }

    @GetMapping("/list")
    public List<FileDto> list(){
        FileDto fileDto1 =
            FileDto.builder().id(1L).fileName("Report.pdf").fileType("PDF").progress(60)
                .fileUrl("fileUrl1").build();
        FileDto fileDto2 =
            FileDto.builder().id(2L).fileName("Image.png").fileType("PNG").progress(20)
                .fileUrl("fileUrl2").build();
        FileDto fileDto3 =
            FileDto.builder().id(1L).fileName("Presentation.pptx").fileType("PPTX").progress(80)
                .fileUrl("fileUrl1").build();
        return List.of(fileDto1,fileDto2,fileDto3);
    }
    @GetMapping("/status")
    public SseEmitter status(@RequestParam(value = "email" , required = false) String email) {
        FileDto fileDto1 =
            FileDto.builder().id(1L).fileName("Report.pdf").progress(60).progress(0)
                .fileSize(560).downloadedSize(0).build();
        FileDto fileDto2 =
            FileDto.builder().id(2L).fileName("Image.png").progress(20).progress(0)
                .fileSize(230).downloadedSize(0).build();

        ArrayList<FileDto> fileList = new ArrayList<>();
        fileList.add(fileDto1);
        fileList.add(fileDto2);
        SseEmitter emitter = new SseEmitter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    SseEmitter.SseEventBuilder event =
                        SseEmitter.event().data(updateFiles(fileList))
                            .id(String.valueOf(i)).name("message");
                    emitter.send(event);
                    Thread.sleep(3000);
                    if(i == 10) break;
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    private List<FileDto> updateFiles(ArrayList<FileDto> fileList) {
        fileList.forEach(file -> {
            file.setDownloadedSize(Math.min(file.getFileSize(),
                generateRandomIntegerInRange(file.getDownloadedSize(), file.getFileSize())));
            file.setProgress(calculateProgress(file.getDownloadedSize(),file.getFileSize()));
        });

        return fileList;
    }

    private int calculateProgress(int downloadedData,int totalData){
        if (totalData == 0) {
            return 0; // Avoid division by zero
        }

        double progressPercentage = (downloadedData * 100.0) / totalData;
        return (int) progressPercentage;
    }

    private Integer generateRandomIntegerInRange(int min, int max) {
        int newMax = Math.min(min+5, max);
        Random random = new Random();
        return random.nextInt(newMax) + min;
    }
}
