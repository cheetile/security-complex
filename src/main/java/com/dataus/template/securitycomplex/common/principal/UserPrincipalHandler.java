package com.dataus.template.securitycomplex.common.principal;

import java.time.Duration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.utils.CookieUtils;
import com.dataus.template.securitycomplex.common.utils.JwtUtils;
import com.dataus.template.securitycomplex.common.utils.RedisUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPrincipalHandler {

    private final AuthenticationManager authenticationManager;

    private final RedisUtils redisUtils;
    private final JwtUtils jwtUtils;

    public UserPrincipal getPrincipal(String username, String password) {
        Authentication authentication = authenticationManager
            .authenticate(
                new UsernamePasswordAuthenticationToken(
                    username, password));
                
        SecurityContextHolder
            .getContext()
            .setAuthentication(authentication);
        
        return (UserPrincipal) authentication.getPrincipal();
        
    }

    public String getAccessToken(
        String currentToken,
        HttpServletRequest request, 
        HttpServletResponse response) {
        
        if(jwtUtils.validateJwtToken(currentToken)) {        
            String refreshToken = CookieUtils
                .getCookie(request, "refreshToken")
                .map(Cookie::getValue)
                .orElseThrow(() -> 
                    ErrorType.CLIENT_REFRESH_TOKEN_NOT_EXIST.getException());

            String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
            String savedRefreshToken = redisUtils.getData(username)
                .orElseThrow(() -> 
                    ErrorType.SERVER_REFRESH_TOKEN_NOT_EXIST.getException());
            
            if(!refreshToken.equals(savedRefreshToken)) {
                CookieUtils.deleteCookie(
                    request, response, "refreshToken");
                redisUtils.deleteData(username);
                throw ErrorType.INVALID_REFRESH_TOKEN.getException();
            }

            return jwtUtils.generateAccessToken(username);
        }

        return null;
    }

    public String getAccessTokenWithProcessLogin(
        HttpServletRequest request, 
        HttpServletResponse response, 
        String username) {

        redisUtils.deleteData(username);

        String refreshToken = jwtUtils
                    .generateRefreshToken(username);

        redisUtils.setDataExipre(
            username, 
            refreshToken, 
            Duration.ofMillis(
                jwtUtils.getExpirationMs(refreshToken)));

        CookieUtils.addCookie(
            response, 
            "refreshToken", 
            refreshToken, 
            (int) (jwtUtils.getExpirationMs(refreshToken)/1000));
        
        return jwtUtils.generateAccessToken(username);
        
    }

    public void processLogout(
        UserPrincipal principal,
        HttpServletRequest request, 
        HttpServletResponse response) {
       
        CookieUtils.getCookie(request, "refreshToken")
            .map(Cookie::getValue)
            .ifPresent(refreshToken -> {
                redisUtils.deleteData(jwtUtils.getUsernameFromJwtToken(refreshToken));
                
                CookieUtils.deleteCookie(request, response, "refreshToken");
            });
        
        String accessToken = principal.getAccessToken();
        redisUtils.setDataExipre(
            accessToken,
            "blacklist",
            Duration.ofMillis(jwtUtils.getExpirationMs(accessToken)));
        
    }
    
}
