package com.cs7rishi.oFile.exception;

import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.utils.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {OFileException.class})
    protected ResponseEntity<GenericResponse<?>> handleOFileException(OFileException exception) {
        return new ResponseEntity<>(
            ApiResponseUtil.error(null, exception.getMessage(), exception.getMessage()),
            HttpStatus.OK);
    }

    //Todo test for Exception class handling
}
