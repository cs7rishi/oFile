package com.cs7rishi.oFile.model.response;

import com.cs7rishi.oFile.dto.FileDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StreamResponse {
    List<FileDto> files;

    public StreamResponse(){
        files = new ArrayList<>();
    }
}
