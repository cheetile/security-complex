package com.dataus.template.securitycomplex.member.controller;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.dataus.template.securitycomplex.common.dto.BaseResponse;
import com.dataus.template.securitycomplex.common.exception.ErrorType;
import com.dataus.template.securitycomplex.common.principal.CurrentMember;
import com.dataus.template.securitycomplex.common.principal.UserPrincipal;
import com.dataus.template.securitycomplex.member.dto.LoginRequest;
import com.dataus.template.securitycomplex.member.dto.MemberResponse;
import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.dto.RegisterRequest;
import com.dataus.template.securitycomplex.member.entity.Member;
import com.dataus.template.securitycomplex.member.enums.RoleType;
import com.dataus.template.securitycomplex.member.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signin")
    public ResponseEntity<?> login(
        HttpServletRequest request, 
        HttpServletResponse response,
        @Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok()
                .body(memberService.login(
                    request, response, loginRequest));                
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout(
        @CurrentMember UserPrincipal principal,
        HttpServletRequest request, 
        HttpServletResponse response) {
        
        return ResponseEntity.ok()
                .body(memberService.logout(principal, request, response));
    }

    @GetMapping("/{username}/exists")
    public ResponseEntity<?> existsUsername(
        @Size(min = 5, max = 20) @NotBlank 
        @PathVariable("username") String username) {
        
        return ResponseEntity.ok()
                .body(memberService.existsUsername(username));
    }

    @PostMapping("/")
    public ResponseEntity<?> register(
        HttpServletRequest request, 
        HttpServletResponse response,
        @Valid @RequestBody RegisterRequest registerRequest) {
          
        BaseResponse<MemberResponse> registerResponse = memberService.register(
            request, response, registerRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/members/{id}")
                .buildAndExpand(registerResponse.getData().getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(registerResponse);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findMember(
        @PathVariable("id") Optional<Member> memberOptional) {
        
        return ResponseEntity.ok()
                .body(MemberResponse.of(
                    memberOptional.orElseThrow(() ->
                        ErrorType.UNAVAILABLE_PAGE
                            .getException())));
    }

    
    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyMember(
        @CurrentMember          UserPrincipal principal,
        @PathVariable("id")     Long id,
        @Valid @RequestBody     ModifyRequest modifyRequest) {
        
        if(!principal.hasRole(id)) {
            throw ErrorType.MEMBER_NO_AUTHORITY
                    .getException();
        }
        
        return ResponseEntity.ok()
                .body(memberService.modifyMember(principal, id, modifyRequest));
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(
        @CurrentMember      UserPrincipal principal,
        @PathVariable("id") Long id) {

        if(!principal.hasRole(id)) {
            throw ErrorType.MEMBER_NO_AUTHORITY
                    .getException();
        }

        return ResponseEntity.ok()
                .body(memberService.deleteMember(principal, id));        
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<?> changeRoles(
        @CurrentMember      UserPrincipal principal,
        @PathVariable("id") Long id,
        @NotEmpty @RequestBody Set<RoleType> roles) {
        
        if(!principal.getAuthorities().contains(RoleType.ROLE_ADMIN)) {
            throw ErrorType.MEMBER_NO_AUTHORITY
                    .getException();
        }

        return ResponseEntity.ok()
                .body(memberService.changeRoles(principal, id, roles));

    }
    
}
