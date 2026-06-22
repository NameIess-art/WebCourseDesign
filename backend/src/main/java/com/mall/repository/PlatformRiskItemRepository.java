package com.mall.repository;

import com.mall.entity.PlatformRiskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformRiskItemRepository extends JpaRepository<PlatformRiskItem, Long> {

    List<PlatformRiskItem> findAllByOrderByCreatedAtDesc();
}
