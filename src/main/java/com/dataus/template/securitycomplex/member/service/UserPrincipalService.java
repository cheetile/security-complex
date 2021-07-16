package com.dataus.template.securitycomplex.member.service;

import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.config.security.oauth2.info.OAuth2UserInfo;
import com.dataus.template.securitycomplex.config.security.oauth2.info.OAuth2UserInfoFactory;
import com.dataus.template.securitycomplex.member.entity.Member;
import com.dataus.template.securitycomplex.member.entity.MemberRole;
import com.dataus.template.securitycomplex.member.enums.RoleType;
import com.dataus.template.securitycomplex.member.repository.MemberRepository;
import com.dataus.template.securitycomplex.member.repository.MemberRoleRepository;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPrincipalService extends DefaultOAuth2UserService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException(String.format(
                    "Member Not Found with id: %s", username)));

        return UserPrincipal.of(member);
    }

    @Override
    public UserPrincipal loadUser(OAuth2UserRequest oAuth2userRequest) throws OAuth2AuthenticationException {
        
        OAuth2User oAuth2User = super.loadUser(oAuth2userRequest);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
            .getOAuth2UserInfo(
                oAuth2userRequest
                    .getClientRegistration()
                    .getRegistrationId(),
                oAuth2User.getAttributes());
        
        Member member = memberRepository
            .findByUsername(oAuth2UserInfo.getUsername())
                .orElseGet(() -> {
                    Member m = memberRepository.save(
                        Member.of(oAuth2UserInfo));
                    memberRoleRepository.save(new MemberRole(
                        m, RoleType.ROLE_USER));
                    
                    return m;
                });

        return UserPrincipal.of(member, oAuth2User.getAttributes());
    }
    
}
