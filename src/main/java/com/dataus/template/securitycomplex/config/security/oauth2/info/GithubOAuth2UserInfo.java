package com.dataus.template.securitycomplex.config.security.oauth2.info;

import java.util.Map;

import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(ProviderType.GITHUB, attributes);
    }

    @Override
    public String getUsername() {
        return provider + "_" + attributes.get("id");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
    
}
