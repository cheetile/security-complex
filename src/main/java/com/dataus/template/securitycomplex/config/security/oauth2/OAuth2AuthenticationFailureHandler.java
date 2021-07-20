package com.dataus.template.securitycomplex.config.security.oauth2;

import static com.dataus.template.securitycomplex.config.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.utils.CookieUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request, 
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {
                
        String targetUrl = UriComponentsBuilder
            .fromUriString(CookieUtils
                            .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                            .map(Cookie::getValue)
                            .orElse(("/")))
            .build()
            .toUriString();
        
        ErrorType.OAUTH_LOGIN_FAIL.sendErrorResponse(request, response);

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
}
