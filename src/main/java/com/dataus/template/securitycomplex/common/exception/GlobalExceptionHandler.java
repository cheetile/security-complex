package com.dataus.template.securitycomplex.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(HttpServletRequest request, CommonException ex) {

        return ResponseEntity
                .status(ex.getErrorType().getHttpStatus())
                .body(new ErrorResponse(request, ex.getErrorType()));        
    }
    
}
