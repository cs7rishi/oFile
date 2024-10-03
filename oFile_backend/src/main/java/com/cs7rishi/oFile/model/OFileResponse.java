package com.cs7rishi.oFile.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class OFileResponse {
    int status;
    String msg;
    String description;
}
