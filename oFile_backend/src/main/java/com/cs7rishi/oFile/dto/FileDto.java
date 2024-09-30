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
    Integer fileSize;
    Integer downloadedSize;
    //speed in MBps
    Integer speed;
    Integer progress;


    public static FileEntity createEntity(FileDto fileDto, String email, Customer customer){
        return FileEntity.builder()
            .userEmail(email)
            .fileName(fileDto.fileName)
            .fileUrl(fileDto.fileUrl)
            .customer(customer)
            .build();
    }
}
