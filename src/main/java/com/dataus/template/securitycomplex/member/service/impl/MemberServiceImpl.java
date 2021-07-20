package com.dataus.template.securitycomplex.member.service.impl;

import java.util.Set;

import javax.transaction.Transactional;

import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;
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
    

    @Override
    public boolean existsUsername(String username) {
        if(memberRepository.existsByUsername(username)) {
            return true;
        }

        return false;
    }

    @Override
    public MemberResponse register(RegisterRequest registerRequest) {
            
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        if(existsUsername(username)) {
            throw ErrorType.REGISTERED_USERNAME.getException();
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
        
        return MemberResponse.of(member);
    }

    @Override
    public void modifyMember(Long id, ModifyRequest modifyRequest) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());

        member.modify(modifyRequest); 
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());

        member.delete();
    }

    @Override
    public void changeRoles(Long id, Set<RoleType> roles) {
        Member member = memberRepository
            .findById(id)
            .orElseThrow(() ->
                ErrorType.NO_MEMBER_ID.getException());
        
        member.deleteRoles();
        roles.forEach(r -> 
            memberRoleRepository.save(new MemberRole(member, r)));

    }

}
