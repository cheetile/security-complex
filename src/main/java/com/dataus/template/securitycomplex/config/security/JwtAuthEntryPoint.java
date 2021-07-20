package com.dataus.template.securitycomplex.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.exception.ErrorType;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) 
        throws IOException, ServletException {

        ErrorType.UNAUTHORIZED_MEMBER.sendErrorResponse(request, response);
    }
    
}
