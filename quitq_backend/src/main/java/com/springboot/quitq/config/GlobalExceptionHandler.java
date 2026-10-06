package com.springboot.quitq.config;

import com.springboot.quitq.dto.response.ErrorDto;
import com.springboot.quitq.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(
            ResourceNotFoundException e
    ){
        //always reply the object not the string

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorDto(e.getMessage(),
                                "Id not found in DB",
                                Instant.now())
                );
    }
}
