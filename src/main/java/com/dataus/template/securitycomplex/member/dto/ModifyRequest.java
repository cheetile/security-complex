package com.dataus.template.securitycomplex.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyRequest {

    @Size(max = 100) @NotBlank
    private String nickname;

    @Size(max = 2083)
    private String image;
    
}
