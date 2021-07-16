package com.dataus.template.securitycomplex.config.security.oauth2.info;

import java.util.Map;

import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;

public abstract class OAuth2UserInfo {

    protected ProviderType provider;

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(ProviderType provider, Map<String, Object> attributes) {
        this.provider = provider;
        this.attributes = attributes;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getUsername();
    
    public abstract String getNickname();
    
    public abstract String getEmail();
    
    public abstract String getImageUrl();
    
}
