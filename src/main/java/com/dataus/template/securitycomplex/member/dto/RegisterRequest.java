package com.dataus.template.securitycomplex.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    
    @Size(min = 5, max = 20) @NotBlank
    private String username;

    @Size(min = 6, max = 30) @NotBlank
    private String password;

    @Size(max = 100) @NotBlank
    private String name;

    @Size(max = 100) @NotBlank
    private String nickname;

    @Email @Size(max = 320)
    private String email;

    @Size(max = 2083)
    private String image;
}
