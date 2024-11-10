package com.cast.caspedia.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionMsgDto> handleAppException(AppException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ExceptionMsgDto(ex.getMessage()));
    }
}

