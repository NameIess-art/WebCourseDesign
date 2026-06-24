package com.mall.repository;

import com.mall.entity.PlatformRiskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformRiskItemRepository extends JpaRepository<PlatformRiskItem, Long> {

    List<PlatformRiskItem> findAllByOrderByCreatedAtDesc();
    Page<PlatformRiskItem> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
