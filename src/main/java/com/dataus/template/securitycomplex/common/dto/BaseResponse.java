package com.dataus.template.securitycomplex.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BaseResponse {

    private boolean success;

    private String message;

    @JsonInclude(Include.NON_NULL)
    private String accessToken;
    
    @JsonInclude(Include.NON_NULL)
    private Object data;
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}