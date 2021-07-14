package com.dataus.template.securitycomplex.common.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter @Setter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "inst_dt", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updt_dt")
    private LocalDateTime lastModifiedDate;

    @Column(name = "del_yn")
    private boolean deleted = false;

    public void delete() {
        this.deleted = true;
    }
    
}
