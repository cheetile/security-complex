package com.dataus.template.securitycomplex.member.service;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataus.template.securitycomplex.common.dto.BaseResponse;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.member.dto.LoginRequest;
import com.dataus.template.securitycomplex.member.dto.MemberResponse;
import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.dto.RegisterRequest;
import com.dataus.template.securitycomplex.member.enums.RoleType;

public interface MemberService {

    BaseResponse<MemberResponse> login(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest);

    BaseResponse<?> logout(UserPrincipal principal, HttpServletRequest request, HttpServletResponse response);

    BaseResponse<?> existsUsername(String username);
    
    BaseResponse<MemberResponse> register(HttpServletRequest request, HttpServletResponse response, RegisterRequest registerRequest);

    BaseResponse<?> modifyMember(UserPrincipal principal, Long id, ModifyRequest modifyRequest);

    BaseResponse<?> deleteMember(UserPrincipal principal, Long id);

    BaseResponse<?> changeRoles(UserPrincipal principal, Long id, Set<RoleType> roles);
    
}
