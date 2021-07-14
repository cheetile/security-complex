package com.dataus.template.securitycomplex.config.audit;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
            
            if(authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            try {
                OAuth2User principal = (OAuth2User) authentication.getPrincipal();

                return Optional.of(principal.getName());
            } catch (ClassCastException e) {} 

            return null;

        };
    }
    
}
