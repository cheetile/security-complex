package com.dataus.template.securitycomplex.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.server.ResponseStatusException;

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
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "Failed to authorize"),
    MEMBER_NO_AUTHORITY(UNAUTHORIZED, "Unauthorized access"),
    NOT_SUPPORTED_PROVIDER(UNAUTHORIZED, "This provider is not supported yet"),
    CLIENT_TOKEN_NOT_EXISTS(UNAUTHORIZED, "Client refresh token doesn't exist"),
    SERVER_TOKEN_NOT_EXISTS(UNAUTHORIZED, "Server refresh token doesn't exist"),
    MEBER_SINGED_OUT(UNAUTHORIZED, "This member signed out"),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "This refresh token can't authenticate"),
    UNAUTHORIZED_REDIRECTION(UNAUTHORIZED, "Unauthorized redirection"),

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
    
    public ResponseStatusException getException() {
        log.error("CustomException ErrorType: {}", this);
        return new ResponseStatusException(this.httpStatus, this.message);
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
