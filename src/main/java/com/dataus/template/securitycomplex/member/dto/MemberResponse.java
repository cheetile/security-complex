package com.dataus.template.securitycomplex.member.dto;

import java.util.Collection;

import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;
import com.dataus.template.securitycomplex.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@JsonPropertyOrder({
    "id", "username", "provider",
    "name", "nickname", "email",
    "image", "roles"
})
public class MemberResponse {

    private Long id;

    private String username;

    private ProviderType provider;

    private String name;

    private String nickname;

    private String email;

    private String image;

    private Collection<? extends GrantedAuthority> roles; 

    @JsonInclude(Include.NON_NULL)
    private String jwt;

    public static MemberResponse of(Member member) {
        return MemberResponse.of(member, null);
    }

    public static MemberResponse of(Member member, String jwt) {
        return MemberResponse.builder()
                    .id(member.getId())
                    .username(member.getUsername())
                    .provider(member.getProvider())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .image(member.getImageUrl())
                    .roles(member.getRoleTypes())
                    .jwt(jwt)
                    .build();
    }
    
}
