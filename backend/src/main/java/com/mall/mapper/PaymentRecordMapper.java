package com.mall.mapper;

import com.mall.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.time.*;
import java.math.*;
import com.mall.enums.*;

@Mapper
public interface PaymentRecordMapper {

    @Select("SELECT * FROM payment_records WHERE id = #{id}")
    @Results(id = "PaymentRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "paymentNo", column = "payment_no"),
        @Result(property = "order", column = "order_id", one = @One(select = "com.mall.mapper.OrderMapper.findById")),
        @Result(property = "channel", column = "channel"),
        @Result(property = "amount", column = "amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "paidAt", column = "paid_at")
    })
    Optional<PaymentRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM payment_records")
    @ResultMap("PaymentRecordMap")
    List<PaymentRecord> findAll();

    @Select("SELECT * FROM payment_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("PaymentRecordMap")
    List<PaymentRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM payment_records")
    long count();

    @Insert("INSERT INTO payment_records (payment_no, order_id, channel, amount, status, created_at, paid_at) VALUES (#{paymentNo}, #{order.id}, #{channel}, #{amount}, #{status}, #{createdAt}, #{paidAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentRecord entity);

    @Update("UPDATE payment_records SET payment_no = #{paymentNo}, order_id = #{order.id}, channel = #{channel}, amount = #{amount}, status = #{status}, created_at = #{createdAt}, paid_at = #{paidAt} WHERE id = #{id}")
    int update(PaymentRecord entity);

    @Delete("DELETE FROM payment_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default PaymentRecord save(PaymentRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<PaymentRecord> entities) {
        for (PaymentRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(PaymentRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<PaymentRecord> entities) {
        for (PaymentRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<PaymentRecord> findAll(Pageable pageable) {
        List<PaymentRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM payment_records</script>")
    @ResultMap("PaymentRecordMap")
    Optional<PaymentRecord> findByPaymentNo(@Param("paymentNo") String paymentNo);

    @Select("<script>SELECT * FROM payment_records WHERE order_id = #{orderId} ORDER BY created_at desc</script>")
    @ResultMap("PaymentRecordMap")
    List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(@Param("orderId") Long orderId);

    @Select("<script>SELECT COALESCE(SUM(amount), 0) FROM payment_records WHERE amount</script>")
    BigDecimal sumPaidAmountBetween(@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);

}
