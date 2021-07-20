package com.dataus.template.securitycomplex.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.exception.CommonException;
import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.common.utils.JwtUtils;
import com.dataus.template.securitycomplex.common.utils.RedisUtils;
import com.dataus.template.securitycomplex.member.service.UserPrincipalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException {
        
        try{            
            String accessToken = parseJwt(request);
            if(accessToken != null &&
               jwtUtils.validateJwtToken(accessToken)) {

                if(redisUtils.getData(accessToken).isPresent())
                    throw ErrorType.MEMBER_SINGED_OUT.getException();

                String username = jwtUtils.getUsernameFromJwtToken(accessToken);
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
        } catch(CommonException ex) {
            ex.getErrorType().sendErrorResponse(request, response);
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
