package com.dataus.template.securitycomplex.member.service.impl;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.dataus.template.securitycomplex.common.dto.BaseResponse;
import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.common.principal.UserPrincipalHandler;
import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;
import com.dataus.template.securitycomplex.member.dto.LoginRequest;
import com.dataus.template.securitycomplex.member.dto.MemberResponse;
import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.dto.RegisterRequest;
import com.dataus.template.securitycomplex.member.entity.Member;
import com.dataus.template.securitycomplex.member.entity.MemberRole;
import com.dataus.template.securitycomplex.member.enums.RoleType;
import com.dataus.template.securitycomplex.member.repository.MemberRepository;
import com.dataus.template.securitycomplex.member.repository.MemberRoleRepository;
import com.dataus.template.securitycomplex.member.service.MemberService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserPrincipalHandler userPrincipalHandler;
    

    @Override
    public BaseResponse<?> existsUsername(String username) {
        if(!memberRepository.existsByUsername(username)) {
            return new BaseResponse<>(
                false, 
                String.format("Not Found username: %s", username));
        }

        return new BaseResponse<>(
            true, 
            String.format("Found username: %s", username));
    }

    @Override
    public BaseResponse<MemberResponse> login(
        HttpServletRequest request, 
        HttpServletResponse response, 
        LoginRequest loginRequest) {
        
        UserPrincipal principal = userPrincipalHandler.getPrincipal(
            loginRequest.getUsername(), 
            loginRequest.getPassword());
        
        String accessToken = userPrincipalHandler
            .getAccessTokenWithProcessLogin(request, response, principal);

        return new BaseResponse<MemberResponse>(
                true, 
                "success to login", 
                accessToken, 
                MemberResponse.of(principal.getMember()));
    }

    @Override
    public BaseResponse<?> logout(
        UserPrincipal principal,
        HttpServletRequest request, 
        HttpServletResponse response) {

        userPrincipalHandler.processLogout(principal, request, response);
        
        return new BaseResponse<>(true, "success to logout");
    }

    @Override
    public BaseResponse<MemberResponse> register(
        HttpServletRequest request, 
        HttpServletResponse response, 
        RegisterRequest registerRequest) {
            
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        if(existsUsername(username).isSuccess()) {
            throw ErrorType.REGISTERED_USERNAME
                    .getResponseStatusException();
        }

        Member member = memberRepository.save(new Member(
            username,
            passwordEncoder.encode(password),
            ProviderType.INTERNAL,
            username,
            registerRequest.getEmail(),
            null));        
        memberRoleRepository.save(new MemberRole(
            member, RoleType.ROLE_USER));
        
        UserPrincipal principal = userPrincipalHandler.getPrincipal(
            username, password);
        
        String accessToken = userPrincipalHandler
            .getAccessTokenWithProcessLogin(request, response, principal);

        return new BaseResponse<MemberResponse>(
            true, 
            "success to login", 
            accessToken, 
            MemberResponse.of(principal.getMember()));
    }

    @Override
    public BaseResponse<?> modifyMember(UserPrincipal principal, Long id, ModifyRequest modifyRequest) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID
                    .getResponseStatusException());

        member.modify(modifyRequest);        
        
        return new BaseResponse<>(
            true, 
            String.format("Modified Member Id[%s]", member.getUsername()),
            principal.getAccessToken());
    }

    @Override
    public BaseResponse<?> deleteMember(UserPrincipal principal, Long id) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID
                    .getResponseStatusException());

        member.delete();
           
        return new BaseResponse<>(
            true, 
            String.format("Deleted Member Id[%s]", member.getUsername()),
            principal.getAccessToken());
    }

    @Override
    public BaseResponse<?> changeRoles(UserPrincipal principal, Long id, Set<RoleType> roles) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID
                    .getResponseStatusException());
        
        member.deleteRoles();
        roles.forEach(r -> 
            memberRoleRepository.save(new MemberRole(member, r)));
        
        return new BaseResponse<>(
            true, 
            String.format(
                "Roles of member Id[%s] set to %s", 
                member.getUsername()),
            principal.getAccessToken());
    }

}
