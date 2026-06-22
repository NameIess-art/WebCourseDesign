package com.mall.repository;

import com.mall.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByPaymentNo(String paymentNo);

    List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
