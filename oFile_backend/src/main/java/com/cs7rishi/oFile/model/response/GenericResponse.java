package com.cs7rishi.oFile.model.response;

import com.cs7rishi.oFile.model.OFileResponse;
import lombok.*;


@Builder
@Data
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {
    T data;
    OFileResponse response;
}
