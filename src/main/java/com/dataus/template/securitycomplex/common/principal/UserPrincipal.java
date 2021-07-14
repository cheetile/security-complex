package com.dataus.template.securitycomplex.common.principal;

import java.util.Collection;
import java.util.Map;

import com.dataus.template.securitycomplex.member.entity.Member;
import com.dataus.template.securitycomplex.member.enums.RoleType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserPrincipal implements OAuth2User, UserDetails {

    private Member member;

    private Map<String, Object> attributes;

    public UserPrincipal(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public UserPrincipal(Member member) {
        this(member, null);
    }

    public static UserPrincipal of(Member member, Map<String, Object> attributes) {
        return new UserPrincipal(member, attributes);
    }

    public static UserPrincipal of(Member member) {
        return new UserPrincipal(member, null);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getRoleTypes();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !member.isDeleted();
    }   

    public Member getMember() {
        return member;
    }

    public boolean hasRole(Long id) {
        return member.getId().longValue() == id.longValue() ||
               member.getRoleTypes().contains(RoleType.ROLE_ADMIN);
    }
    
}
