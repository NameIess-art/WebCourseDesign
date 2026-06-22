package com.mall.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "content_audit_items")
public class ContentAuditItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String contentType;

    @Column(nullable = false, length = 120)
    private String target;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
