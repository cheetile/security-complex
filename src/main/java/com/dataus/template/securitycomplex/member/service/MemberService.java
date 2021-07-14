package com.dataus.template.securitycomplex.member.service;

import java.util.Set;

import com.dataus.template.securitycomplex.common.dto.BaseResponse;
import com.dataus.template.securitycomplex.member.dto.LoginRequest;
import com.dataus.template.securitycomplex.member.dto.MemberResponse;
import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.dto.RegisterRequest;
import com.dataus.template.securitycomplex.member.enums.RoleType;

public interface MemberService {

    MemberResponse login(LoginRequest loginRequest);

    BaseResponse existsUsername(String username);
    
    MemberResponse register(RegisterRequest registerRequest);

    BaseResponse modifyMember(Long id, ModifyRequest modifyRequest);

    BaseResponse deleteMember(Long id);

    BaseResponse changeRoles(Long id, Set<RoleType> roles);
    
}
