package com.cs7rishi.oFile.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ResponseStatus {
    int status;
    String msg;
    String description;
}
