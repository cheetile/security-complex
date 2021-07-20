package com.dataus.template.securitycomplex.common.exception;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="Asia/Seoul")
    private Date timestamp = new Date();
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;

    public ErrorResponse(HttpServletRequest request, ErrorType errorType) {
        status = errorType.getHttpStatus().value();
        error = errorType.getHttpStatus().name();
        code = errorType.name();
        message = errorType.getMessage();
        path = request.getRequestURI();
    }
    
}
