package com.dataus.template.securitycomplex.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 5, max = 20) @NotBlank
    private String username;

    @Size(min = 6, max = 30) @NotBlank
    private String password;
    
}
