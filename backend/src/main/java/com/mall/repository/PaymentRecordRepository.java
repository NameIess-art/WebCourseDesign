package com.mall.repository;

import com.mall.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByPaymentNo(String paymentNo);

    List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    @Query("""
            select coalesce(sum(p.amount), 0)
              from PaymentRecord p
             where p.status = 'PAID'
               and p.paidAt is not null
               and p.paidAt >= :startAt
               and p.paidAt < :endAt
            """)
    BigDecimal sumPaidAmountBetween(@Param("startAt") LocalDateTime startAt,
                                    @Param("endAt") LocalDateTime endAt);
}
