package com.cs7rishi.oFile.model.request;

import lombok.Data;

import java.util.List;

@Data
public class StreamRequest {
    List<Long> ids;

    public StreamRequest(List<Long> ids) {
        this.ids = ids;
    }
}
