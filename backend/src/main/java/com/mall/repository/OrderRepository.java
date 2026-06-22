package com.mall.repository;

import com.mall.entity.OrderEntity;
import com.mall.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByStatusIn(List<OrderStatus> statuses);

    @Query("select coalesce(sum(o.totalAmount), 0) from OrderEntity o where o.status in :statuses")
    BigDecimal sumTotalAmountByStatusIn(@Param("statuses") List<OrderStatus> statuses);

    @Query("""
            select count(o)
              from OrderEntity o
             where o.paidAt is not null
               and o.paidAt >= :startAt
               and o.paidAt < :endAt
            """)
    long countPaidOrdersBetween(@Param("startAt") LocalDateTime startAt,
                                @Param("endAt") LocalDateTime endAt);

    @Query("""
            select coalesce(sum(o.totalAmount), 0)
              from OrderEntity o
             where o.paidAt is not null
               and o.paidAt >= :startAt
               and o.paidAt < :endAt
            """)
    BigDecimal sumPaidOrderAmountBetween(@Param("startAt") LocalDateTime startAt,
                                         @Param("endAt") LocalDateTime endAt);
}
