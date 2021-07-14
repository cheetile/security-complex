package com.dataus.template.securitycomplex.config.security.oauth2.info;

import java.util.Map;

import com.dataus.template.securitycomplex.common.exception.ErrorType;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
        String registrationId,
        Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if(registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        } else if(registrationId.equalsIgnoreCase("facebook")) {
            return new FacebookOAuth2UserInfo(attributes);
        } else {
            throw ErrorType.NOT_SUPPORTED_PROVIDER.getException();
        }
                
    }
    
}
