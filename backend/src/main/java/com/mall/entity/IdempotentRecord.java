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
@Table(name = "idempotent_records")
public class IdempotentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String idemKey;

    @Column(nullable = false, length = 40)
    private String bizType;

    @Column(nullable = false, length = 40)
    private String bizResult;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
