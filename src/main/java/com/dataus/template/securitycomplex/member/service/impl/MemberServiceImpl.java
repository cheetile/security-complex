package com.dataus.template.securitycomplex.member.service.impl;

import java.util.Set;

import javax.transaction.Transactional;

import com.dataus.template.securitycomplex.common.dto.BaseResponse;
import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.common.principal.UserPrincipalHandler;
import com.dataus.template.securitycomplex.common.utils.JwtUtils;
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

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserPrincipalHandler userPrincipalHandler;
    

    @Override
    public BaseResponse existsUsername(String username) {
        if(!memberRepository.existsByUsername(username)) {
            return BaseResponse.builder()
                    .success(false)
                    .message(String.format("Not Found username: %s", username))
                    .build();
        }

        return BaseResponse.builder()
                .success(true)
                .message(String.format("Found username: %s", username))
                .build();
    }

    @Override
    public MemberResponse login(LoginRequest loginRequest) {
        UserPrincipal principal = userPrincipalHandler.getPrincipal(
            loginRequest.getUsername(), 
            loginRequest.getPassword());
        
        String jwt = jwtUtils.generateJwtToken(principal);

        return MemberResponse.of(principal.getMember(), jwt);
    }

    @Override
    public MemberResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        if(existsUsername(username).isSuccess()) {
            throw ErrorType.REGISTERED_USERNAME.getException();
        }

        Member member = memberRepository.save(new Member(
            username,
            passwordEncoder.encode(password),
            ProviderType.INTERNAL,
            registerRequest.getName(),
            registerRequest.getNickname(),
            registerRequest.getEmail(),
            registerRequest.getImage()));
        
        memberRoleRepository.save(new MemberRole(
            member, RoleType.ROLE_USER));
        
        
        String jwt = jwtUtils.generateJwtToken(
            userPrincipalHandler.getPrincipal(username, password));

        return MemberResponse.of(member, jwt);
    }

    @Override
    public BaseResponse modifyMember(Long id, ModifyRequest modifyRequest) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());

        member.modify(modifyRequest);        
        
        return BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Modified Member Id[%s]", 
                        member.getUsername()))
                    .build();
    }

    @Override
    public BaseResponse deleteMember(Long id) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());

        member.delete();
           
        return BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Deleted Member Id[%s]", 
                        member.getUsername()))
                    .build();
    }

    @Override
    public BaseResponse changeRoles(Long id, Set<RoleType> roles) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());
        
        member.deleteRoles();
        roles.forEach(r -> 
            memberRoleRepository.save(new MemberRole(member, r)));
        
        return BaseResponse.builder()
            .success(true)
            .message(String.format(
                "Roles of member Id[%s] set to %s", 
                member.getUsername(),
                member.getRoleTypes()))
            .build();
    }

}
