package com.dataus.template.securitycomplex.common.utils;

import java.util.Date;

import com.dataus.template.securitycomplex.common.property.AppProperties;
import com.dataus.template.securitycomplex.common.property.AppProperties.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {

    private String jwtSecret;
    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;

    @Autowired
    public JwtUtils(AppProperties appProperties) {
        Auth auth = appProperties.getAuth();

        this.jwtSecret = auth.getJwtSecret();
        this.accessTokenExpirationMs = auth.getAccessTokenExpirationMs();
        this.refreshTokenExpirationMs = auth.getRefreshTokenExpirationMs();    
    }

    public String generateAccessToken(String username) {
        return generateToken(
            username,
            accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return generateToken(
            username, 
            refreshTokenExpirationMs);
    }

    private String generateToken(String username, long expirationMs) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(
                    SignatureAlgorithm.HS512, 
                    jwtSecret)
                .compact();        
    }

    public boolean validateJwtToken(String token) {
        try {
            extractAllClaims(token);
            
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return true;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String getUsernameFromJwtToken(String token) {
        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            extractAllClaims(token);
            
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public int getExpirationMs(String token) {
        return (int) (extractAllClaims(token)
                        .getExpiration()
                        .getTime() -
                      new Date().getTime());
    }

    private Claims extractAllClaims(String token) 
        throws JwtException, IllegalArgumentException {
        
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    };
    
}
