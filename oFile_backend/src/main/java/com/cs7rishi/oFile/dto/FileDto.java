package com.cs7rishi.oFile.dto;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {

    Long id;
    String fileName;
    String fileUrl;
    Long fileSize;
    Integer progress;

    public static FileEntity createEntity(FileDto fileDto, Customer customer){
        return FileEntity.builder()
            .userEmail(customer.getEmail())
            .fileName(fileDto.getFileName())
            .fileUrl(fileDto.getFileUrl())
            .fileSize(fileDto.getFileSize())
            .progress(0)
            .customer(customer)
            .build();
    }
}
