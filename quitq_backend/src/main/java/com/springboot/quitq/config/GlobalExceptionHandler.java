package com.springboot.quitq.config;

import com.springboot.quitq.dto.response.ErrorDto;
import com.springboot.quitq.exception.DuplicateEntryException;
import com.springboot.quitq.exception.InsufficientStockException;
import com.springboot.quitq.exception.InvalidCallException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(
            ResourceNotFoundException e
    ){

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorDto(e.getMessage(),
                                "Id not found in DB",
                                Instant.now())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        Map<String,String> map = new HashMap<>();

        BindingResult bindingResult =  e.getBindingResult();
        List<FieldError> list = bindingResult.getFieldErrors();
        list.forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity
                .badRequest()
                .body(map );
    }

    @ExceptionHandler(InvalidCallException.class)
    public ResponseEntity<ErrorDto> handleInvalidCallException(
            InvalidCallException e
    ){

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorDto(e.getMessage(),
                                "Call is Invalid due to malformed data",
                                Instant.now())
                );
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorDto> handleInsufficientStockException(
            InsufficientStockException e
    ){

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorDto(e.getMessage(),
                                "Stock not available for this product",
                                Instant.now())
                );
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEntryException(
            DuplicateEntryException e
    ){

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorDto(e.getMessage(),
                                "Record already exists in DB",
                                Instant.now())
                );
    }
}

