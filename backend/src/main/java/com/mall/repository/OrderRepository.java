package com.mall.repository;

import com.mall.entity.OrderEntity;
import com.mall.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(OrderStatus status);
}
