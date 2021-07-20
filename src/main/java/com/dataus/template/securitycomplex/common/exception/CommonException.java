package com.dataus.template.securitycomplex.common.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private ErrorType errorType;

    public CommonException(ErrorType errorType) {
        this.errorType = errorType;
    }
    
}
