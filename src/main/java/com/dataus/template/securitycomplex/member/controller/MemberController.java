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
import com.dataus.template.securitycomplex.common.principal.UserPrincipalHandler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserPrincipalHandler userPrincipalHandler;

    @PostMapping("/signin")
    public ResponseEntity<?> login(
        HttpServletRequest request, 
        HttpServletResponse response,
        @Valid @RequestBody LoginRequest loginRequest) {

        UserPrincipal principal = userPrincipalHandler.getPrincipal(
            loginRequest.getUsername(), 
            loginRequest.getPassword());
        
        String accessToken = userPrincipalHandler
            .getAccessTokenWithProcessLogin(request, response, principal);

        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message("success to login")
                    .accessToken(accessToken)
                    .data(MemberResponse.of(principal.getMember()))
                    .build());
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout(
        @CurrentMember UserPrincipal principal,
        HttpServletRequest request, 
        HttpServletResponse response) {

        userPrincipalHandler.processLogout(principal, request, response);
        
        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message("Success to logout")
                    .build());
    }

    
    @GetMapping("/token")
    public ResponseEntity<?> getAccessToken(
        @NotEmpty @RequestParam("current") String currentToken,
        HttpServletRequest request, 
        HttpServletResponse response) {
        
        String accessToken = userPrincipalHandler
            .getAccessToken(currentToken, request, response);
        
        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message("Issued new access token")
                    .accessToken(accessToken)
                    .build());
    }

    @GetMapping("/{username}/exists")
    public ResponseEntity<?> existsUsername(
        @Size(min = 5, max = 20) @NotBlank 
        @PathVariable("username") String username) {

        boolean exists = memberService.existsUsername(username);
        String message = exists ? 
            String.format("Exists username[%s]", username) : 
            String.format("Doesn't exist username[%s]", username);
        
        return ResponseEntity.ok().body(
                BaseResponse.builder()
                        .success(exists)
                        .message(message)
                        .build());
    }

    @PostMapping("/")
    public ResponseEntity<?> register(
        HttpServletRequest request, 
        HttpServletResponse response,
        @Valid @RequestBody RegisterRequest registerRequest) {
          
        memberService.register(registerRequest);
        
        UserPrincipal principal = userPrincipalHandler.getPrincipal(
            registerRequest.getUsername(),
            registerRequest.getPassword());

        String accessToken = userPrincipalHandler
                .getAccessTokenWithProcessLogin(request, response, principal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/members/{id}")
                .buildAndExpand(principal.getMember().getId())
                .toUri();

        return ResponseEntity.created(location).body(
                BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Member username[%s] registered, generated Id[%d]", 
                        principal.getUsername(), 
                        principal.getMember().getId()))
                    .accessToken(accessToken)
                    .data(MemberResponse.of(principal.getMember()))
                    .build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findMember(
        @PathVariable("id") Optional<Member> memberOptional) {
        
        return ResponseEntity.ok()
                .body(MemberResponse.of(
                    memberOptional.orElseThrow(() ->
                        ErrorType.UNAVAILABLE_PAGE.getException())));
    }

    
    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyMember(
        @CurrentMember          UserPrincipal principal,
        @PathVariable("id")     Long id,
        @Valid @RequestBody     ModifyRequest modifyRequest) {
        
        if(!principal.hasRole(id))
            throw ErrorType.MEMBER_NO_AUTHORITY.getException();        
        
        memberService.modifyMember(id, modifyRequest);

        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Modified Member username[%s]", 
                        principal.getUsername()))
                    .build());
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(
        @CurrentMember      UserPrincipal principal,
        @PathVariable("id") Long id) {

        if(!principal.hasRole(id)) {
            throw ErrorType.MEMBER_NO_AUTHORITY.getException();
        }

        memberService.deleteMember(id);

        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Deleted Member username[%s]", 
                        principal.getUsername()))
                    .build());        
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<?> changeRoles(
        @CurrentMember      UserPrincipal principal,
        @PathVariable("id") Long id,
        @NotEmpty @RequestBody Set<RoleType> roles) {
        
        if(!principal.getAuthorities().contains(RoleType.ROLE_ADMIN)) {
            throw ErrorType.MEMBER_NO_AUTHORITY.getException();
        }

        memberService.changeRoles(id, roles);

        return ResponseEntity.ok().body(
                BaseResponse.builder()
                    .success(true)
                    .message(String.format(
                        "Roles of member username[%s] set to %s", 
                        principal.getUsername()))
                    .build());

    }
    
}
