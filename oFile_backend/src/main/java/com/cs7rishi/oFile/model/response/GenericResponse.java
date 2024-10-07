package com.cs7rishi.oFile.model.response;

import com.cs7rishi.oFile.model.ResponseStatus;
import lombok.*;


@Builder
@Data
@AllArgsConstructor
public class GenericResponse<T> {
    T data;
    ResponseStatus response;
}
