package com.dataus.template.securitycomplex.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseResponse<T> {

    private boolean success;

    private String message;

    @JsonInclude(Include.NON_NULL)
    private String accessToken;
    
    @JsonInclude(Include.NON_NULL)
    private T data;

    public BaseResponse(boolean success, String message, String accessToken) {
        this(success, message, accessToken, null);
    }

    public BaseResponse(boolean success, String message) {
        this(success, message, null);
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
}
