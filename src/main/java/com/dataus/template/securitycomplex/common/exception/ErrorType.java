package com.dataus.template.securitycomplex.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum ErrorType {

    // 400 BAD_REQUEST
    REGISTERED_USERNAME(BAD_REQUEST, "This username already registered"),
    
    // 401 UNAUTHORIZED
    OAUTH_LOGIN_FAIL(UNAUTHORIZED, "Oauth login Failed"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "Failed to authorize"),
    NOT_SUPPORTED_PROVIDER(UNAUTHORIZED, "This provider is not supported yet"),
    CLIENT_REFRESH_TOKEN_NOT_EXIST(UNAUTHORIZED, "Client refresh token doesn't exist"),
    SERVER_REFRESH_TOKEN_NOT_EXIST(UNAUTHORIZED, "Server refresh token doesn't exist"),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "This refresh token can't authenticate"),
    MEMBER_SINGED_OUT(UNAUTHORIZED, "This member signed out"),
    UNAUTHORIZED_REDIRECTION(UNAUTHORIZED, "Unauthorized redirection"),
    ACCESS_TOKEN_EXPIRED(UNAUTHORIZED, "Access token expired"),
    INVALID_TOKEN_REQUEST(UNAUTHORIZED, "Current access token is invalid or not expired"),
    
    // 403 FORBIDDEN
    MEMBER_NO_AUTHORITY(FORBIDDEN, "Unauthorized access"),

    // 404 NOT_FOUND
    UNAVAILABLE_PAGE(NOT_FOUND, "This page not found"),
    NO_MEMBER_ID(NOT_FOUND, "This member id doesn't exist"),

    // 409 CONFLICT
    INVALID_PROVIDER_CODE(CONFLICT, "Provider code conflicts"),
    INVALID_ROLE_CODE(CONFLICT, "Role code conflicts"),
    CONFLICT_REGISTER_MEMBER(CONFLICT, "Registering member conflicts")
    ;

    private HttpStatus httpStatus;
    private String message;
    
    public CommonException getException() {
        log.error("CommonException ErrorType: {}", this);
        return new CommonException(this);
    }

    public void sendErrorResponse(
        HttpServletRequest request, 
        HttpServletResponse response) throws IOException {
        
        log.error("CommonException ErrorType: {}", this);

        String json = new ObjectMapper().writeValueAsString(
                new ErrorResponse(request, this));
        response.setStatus(this.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.flushBuffer();

    }

    @Override
    public String toString() {
        return String.format(
            "[%s(%s, %s)]", 
            this.name(), 
            this.httpStatus, 
            this.message);
    }
    
}
