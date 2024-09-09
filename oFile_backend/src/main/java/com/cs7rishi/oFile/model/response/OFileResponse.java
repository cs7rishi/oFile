package com.cs7rishi.oFile.model.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OFileResponse <T>{
    T data;
    String message;
    String description;
}
