package com.cs7rishi.oFile.dto;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
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
    Long fileSize;
    Long downloadedSize;
    //speed in MBps
    Integer speed;
    Integer progress;


    public static FileEntity createEntity(FileDto fileDto, Customer customer){
        return FileEntity.builder()
            .userEmail(customer.getEmail())
            .fileName(fileDto.fileName)
            .fileUrl(fileDto.fileUrl)
            .progress(0)
            .customer(customer)
            .build();
    }
}
