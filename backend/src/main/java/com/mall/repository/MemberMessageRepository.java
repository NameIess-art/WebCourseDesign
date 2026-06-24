package com.mall.repository;

import com.mall.entity.MemberMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    List<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserIdAndReadFlagFalse(Long userId);
}
