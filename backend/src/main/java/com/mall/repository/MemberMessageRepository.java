package com.mall.repository;

import com.mall.entity.MemberMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    List<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFlagFalse(Long userId);
}
