package com.dataus.template.securitycomplex.config.security.oauth2.info;

import java.util.Map;

import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(ProviderType.GOOGLE, attributes);
    }
    
    @Override
    public String getUsername() {
        return provider + "_" + attributes.get("sub");
    }
    
    @Override
    public String getNickname() {
        String name = (String) attributes.get("name");
        return name != null ? name 
                            : ((String) attributes.get("email")).split("@")[0];
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
    
}
