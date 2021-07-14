package com.dataus.template.securitycomplex.common.principal;

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
    
}
