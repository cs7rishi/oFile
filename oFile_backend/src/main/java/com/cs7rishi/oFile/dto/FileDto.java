package com.cs7rishi.oFile.dto;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class FileDto {

    Long id;
    String userId;
    String fileName;
    String fileUrl;
    String fileType;
    Integer fileSize;
    Integer downloadedSize;
    //speed in MBps
    Integer speed;
    Integer progress;

}
