package com.dataus.template.securitycomplex.member.service;

import java.util.Set;

import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.dto.RegisterRequest;
import com.dataus.template.securitycomplex.member.enums.RoleType;

public interface MemberService {

    boolean existsUsername(String username);
    
    void register(RegisterRequest registerRequest);

    void modifyMember(Long id, ModifyRequest modifyRequest);

    void deleteMember(Long id);

    void changeRoles(Long id, Set<RoleType> roles);

    
}
