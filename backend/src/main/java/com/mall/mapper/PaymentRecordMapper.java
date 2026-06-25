package com.mall.mapper;

import com.mall.entity.PaymentRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentRecordMapper extends MyBatisMapperSupport<PaymentRecord> {

    public PaymentRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, PaymentRecord.class);
    }

    public Optional<PaymentRecord> findByPaymentNo(String paymentNo) {
        return Optional.ofNullable(selectOne("t.payment_no = #{params.paymentNo}", params("paymentNo", paymentNo)));
    }

    public List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(Long orderId) {
        return selectList("t.order_id = #{params.orderId}", params("orderId", orderId), "t.created_at desc");
    }

    public BigDecimal sumPaidAmountBetween(LocalDateTime startAt, LocalDateTime endAt) {
        return sumWhere("t.amount", "t.status = 'PAID' and t.paid_at is not null and t.paid_at >= #{params.startAt} and t.paid_at < #{params.endAt}", params("startAt", startAt, "endAt", endAt));
    }
}
