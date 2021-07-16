package com.dataus.template.securitycomplex.common.principal;

import java.time.Duration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public String getAccessTokenWithProcessLogin(
        HttpServletRequest request, 
        HttpServletResponse response, 
        UserPrincipal principal) {

        String username = principal.getUsername();

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
                String accessToken = principal.getAccessToken();
                redisUtils.setDataExipre(
                    accessToken,
                    "blacklist",
                    Duration.ofMillis(jwtUtils.getExpirationMs(accessToken)));
                redisUtils.deleteData(jwtUtils.getUsernameFromJwtToken(refreshToken));
                
                CookieUtils.deleteCookie(request, response, "refreshToken");
            });
        
    }
    
}
