package com.cs7rishi.oFile.utils;

import com.cs7rishi.oFile.model.OFileResponse;
import com.cs7rishi.oFile.model.response.GenericResponse;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public class ApiResponseUtil {
    public static <T> GenericResponse<T> success(T data, String msg, String description) {
       return GenericResponse.<T>builder()
           .response(generateOFileResponse(HttpStatus.OK,msg,description))
           .data(data)
           .build();
    }
    public static <T> GenericResponse<T> error(T data, String msg, String description) {
        return GenericResponse.<T>builder()
            .response(generateOFileResponse(HttpStatus.INTERNAL_SERVER_ERROR,msg,description))
            .data(data)
            .build();
    }
    private static OFileResponse generateOFileResponse(HttpStatus httpStatus, String msg,
        String description) {
        return OFileResponse.builder()
            .status(httpStatus.value())
            .description(description)
            .msg(msg)
            .build();
    }
}
