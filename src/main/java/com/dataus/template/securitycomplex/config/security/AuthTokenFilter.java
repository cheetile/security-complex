package com.dataus.template.securitycomplex.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.common.utils.CookieUtils;
import com.dataus.template.securitycomplex.common.utils.JwtUtils;
import com.dataus.template.securitycomplex.common.utils.RedisUtils;
import com.dataus.template.securitycomplex.member.service.UserPrincipalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserPrincipalService userPrincipalService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            String username = null;
            
            String accessToken = parseJwt(request);
            if(accessToken != null && 
               jwtUtils.validateJwtToken(accessToken)) {

                if(redisUtils.getData(accessToken).isPresent())
                    throw ErrorType.MEBER_SINGED_OUT
                            .getAuthenticationException();

                if(jwtUtils.isTokenExpired(accessToken)) {
                    String refreshToken = CookieUtils
                        .getCookie(request, "refreshToken")
                        .map(Cookie::getValue)
                        .orElseThrow(() -> 
                            ErrorType.CLIENT_TOKEN_NOT_EXISTS
                                .getAuthenticationException());

                    username = jwtUtils.getUsernameFromJwtToken(refreshToken);
                    String savedRefreshToken = redisUtils.getData(username)
                        .orElseThrow(() -> 
                            ErrorType.SERVER_TOKEN_NOT_EXISTS
                                .getAuthenticationException());
                    
                    if(!refreshToken.equals(savedRefreshToken)) {
                        CookieUtils.deleteCookie(
                            request, response, "refreshToken");
                        redisUtils.deleteData(username);
                        throw ErrorType.INVALID_REFRESH_TOKEN
                                .getAuthenticationException();
                    }

                    accessToken = jwtUtils.generateAccessToken(username);
                }

                username = jwtUtils.getUsernameFromJwtToken(accessToken);
                UserPrincipal principal = userPrincipalService
                        .loadUserByUsername(username);
                principal.setAccessToken(accessToken);

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        principal, 
                        null, 
                        principal.getAuthorities());

                authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                
                SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch(AuthenticationException ex) {
            response.sendError(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage());
        }

    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && 
           headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
    
}
