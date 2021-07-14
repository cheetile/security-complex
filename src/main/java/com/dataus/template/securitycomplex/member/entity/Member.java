package com.dataus.template.securitycomplex.member.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.dataus.template.securitycomplex.common.entity.BaseEntity;
import com.dataus.template.securitycomplex.config.security.oauth2.info.OAuth2UserInfo;
import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;
import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.converter.ProviderTypeConverter;
import com.dataus.template.securitycomplex.member.dto.ModifyRequest;
import com.dataus.template.securitycomplex.member.enums.RoleType;

import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NamedEntityGraph(
    name = "Member.role",
    attributeNodes = @NamedAttributeNode("roles"))
@Entity
@Table(name = "tb_members")
@Where(clause = "del_yn = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String username;

    @Column(length = 100)
    private String password;

    @Convert(converter = ProviderTypeConverter.class)
    @Column(name = "provider_cd", length = 2)
    private ProviderType provider;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String nickname;

    @Column(length = 320)
    private String email;

    @Column(name = "img_url", length = 2083)
    private String imageUrl;

    @OneToMany(mappedBy = "member")
    private List<MemberRole> roles = new ArrayList<>();


    public Member(String username, ProviderType provider, String name, String nickname, String email, String imageUrl) {
        this(username, null, provider, name, nickname, email, imageUrl);
    }

    public Member(String username, String password, ProviderType provider, String name, String nickname, String email, String imageUrl) {
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public static Member of(OAuth2UserInfo oAuth2UserInfo) {
        return new Member(
            oAuth2UserInfo.getUsername(), 
            oAuth2UserInfo.getProvider(), 
            oAuth2UserInfo.getName(), 
            oAuth2UserInfo.getNickname(), 
            oAuth2UserInfo.getEmail(),
            oAuth2UserInfo.getImageUrl());
    }

    @Override
    public void delete() {
        this.roles.forEach(MemberRole::delete);
        this.setDeleted(true);
    }

    @PrePersist
    public void prePersist() {
        this.setCreatedBy(this.username);
        this.setLastModifiedBy(this.username);
    }

    public void modify(ModifyRequest modifyRequest) {
        this.name = modifyRequest.getName();
        this.nickname = modifyRequest.getNickname();
        this.email = modifyRequest.getEmail();
        this.imageUrl = modifyRequest.getImage();
    }
    
    public Set<RoleType> getRoleTypes() {
        return this.roles.stream()
                .map(MemberRole::getRole)
                .collect(Collectors.toSet());
    }

    public void deleteRoles() {
        this.roles.forEach(MemberRole::delete);
        this.roles.clear();
    }

}
