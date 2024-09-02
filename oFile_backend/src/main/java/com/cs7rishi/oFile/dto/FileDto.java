package com.cs7rishi.oFile.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDto {

    Long id;
    String userId;
    String fileName;
    String fileUrl;
}
