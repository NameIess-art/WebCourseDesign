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
public interface OrderMapper {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results(id = "OrderEntityMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "originalAmount", column = "original_amount"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "pointsUsed", column = "points_used"),
        @Result(property = "pointsDiscountAmount", column = "points_discount_amount"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "paidAt", column = "paid_at"),
        @Result(property = "paymentChannel", column = "payment_channel"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "merchantRemark", column = "merchant_remark"),
        @Result(property = "idempotencyKey", column = "idempotency_key"),
        @Result(property = "merchant", column = "merchant_id", one = @One(select = "com.mall.mapper.MerchantMapper.findById")),
        @Result(property = "items", column = "id", many = @Many(select = "com.mall.mapper.OrderItemMapper.findByOrderId"))
    })
    Optional<OrderEntity> findById(@Param("id") Long id);

    @Select("SELECT * FROM orders")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findAll();

    @Select("SELECT * FROM orders LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM orders")
    long count();

    @Insert("INSERT INTO orders (order_no, user_id, total_amount, original_amount, discount_amount, points_used, points_discount_amount, shipping_address, status, created_at, paid_at, payment_channel, audit_status, merchant_remark, idempotency_key, merchant_id) VALUES (#{orderNo}, #{user.id}, #{totalAmount}, #{originalAmount}, #{discountAmount}, #{pointsUsed}, #{pointsDiscountAmount}, #{shippingAddress}, #{status}, #{createdAt}, #{paidAt}, #{paymentChannel}, #{auditStatus}, #{merchantRemark}, #{idempotencyKey}, #{merchant.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderEntity entity);

    @Update("UPDATE orders SET order_no = #{orderNo}, user_id = #{user.id}, total_amount = #{totalAmount}, original_amount = #{originalAmount}, discount_amount = #{discountAmount}, points_used = #{pointsUsed}, points_discount_amount = #{pointsDiscountAmount}, shipping_address = #{shippingAddress}, status = #{status}, created_at = #{createdAt}, paid_at = #{paidAt}, payment_channel = #{paymentChannel}, audit_status = #{auditStatus}, merchant_remark = #{merchantRemark}, idempotency_key = #{idempotencyKey}, merchant_id = #{merchant.id} WHERE id = #{id}")
    int update(OrderEntity entity);

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default OrderEntity save(OrderEntity entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<OrderEntity> entities) {
        for (OrderEntity entity : entities) {
            save(entity);
        }
    }

    default void delete(OrderEntity entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<OrderEntity> entities) {
        for (OrderEntity entity : entities) {
            delete(entity);
        }
    }

    default Page<OrderEntity> findAll(Pageable pageable) {
        List<OrderEntity> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at desc</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findByUserIdOrderByCreatedAtDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    default Page<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        List<OrderEntity> content = findByUserIdOrderByCreatedAtDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByCreatedAtDesc(userId));
    }

    @Select("<script>SELECT * FROM orders ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findAllByOrderByCreatedAtDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE user_id = #{userId}</script>")
    long countFindAllByOrderByCreatedAtDesc();

    default Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        List<OrderEntity> content = findAllByOrderByCreatedAtDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindAllByOrderByCreatedAtDesc());
    }

    @Select("<script>SELECT * FROM orders WHERE merchant_id = #{merchantId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findByMerchantIdOrderByCreatedAtDescPage(@Param("merchantId") Long merchantId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE merchant_id = #{merchantId}</script>")
    long countFindByMerchantIdOrderByCreatedAtDesc(@Param("merchantId") Long merchantId);

    default Page<OrderEntity> findByMerchantIdOrderByCreatedAtDesc(Long merchantId, Pageable pageable) {
        List<OrderEntity> content = findByMerchantIdOrderByCreatedAtDescPage(merchantId, pageable);
        return new PageImpl<>(content, pageable, countFindByMerchantIdOrderByCreatedAtDesc(merchantId));
    }

    @Select("<script>SELECT * FROM orders WHERE status = #{status} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findByStatusOrderByCreatedAtDescPage(@Param("status") OrderStatus status, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE status = #{status}</script>")
    long countFindByStatusOrderByCreatedAtDesc(@Param("status") OrderStatus status);

    default Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable) {
        List<OrderEntity> content = findByStatusOrderByCreatedAtDescPage(status, pageable);
        return new PageImpl<>(content, pageable, countFindByStatusOrderByCreatedAtDesc(status));
    }

    @Select("<script>SELECT * FROM orders WHERE merchant_id = #{merchantId} AND status = #{status} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("OrderEntityMap")
    List<OrderEntity> findByMerchantIdAndStatusOrderByCreatedAtDescPage(@Param("merchantId") Long merchantId, @Param("status") OrderStatus status, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE merchant_id = #{merchantId} AND status = #{status}</script>")
    long countFindByMerchantIdAndStatusOrderByCreatedAtDesc(@Param("merchantId") Long merchantId, @Param("status") OrderStatus status);

    default Page<OrderEntity> findByMerchantIdAndStatusOrderByCreatedAtDesc(Long merchantId, OrderStatus status, Pageable pageable) {
        List<OrderEntity> content = findByMerchantIdAndStatusOrderByCreatedAtDescPage(merchantId, status, pageable);
        return new PageImpl<>(content, pageable, countFindByMerchantIdAndStatusOrderByCreatedAtDesc(merchantId, status));
    }

    @Select("<script>SELECT COUNT(*) FROM orders WHERE status = #{status}</script>")
    long countByStatus(@Param("status") OrderStatus status);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE merchant_id = #{merchantId}</script>")
    long countByMerchantId(@Param("merchantId") Long merchantId);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE merchant_id = #{merchantId} AND status = #{status}</script>")
    long countByMerchantIdAndStatus(@Param("merchantId") Long merchantId, @Param("status") OrderStatus status);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE status IN <foreach collection='statuses' item='status' open='(' separator=',' close=')'>#{status}</foreach></script>")
    long countByStatusIn(@Param("statuses") List<OrderStatus> statuses);

    @Select("<script>SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN <foreach collection='statuses' item='status' open='(' separator=',' close=')'>#{status}</foreach></script>")
    BigDecimal sumTotalAmountByStatusIn(@Param("statuses") List<OrderStatus> statuses);

    @Select("<script>SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE merchant_id = #{merchantId} AND status IN <foreach collection='statuses' item='status' open='(' separator=',' close=')'>#{status}</foreach></script>")
    BigDecimal sumTotalAmountByMerchantIdAndStatusIn(@Param("merchantId") Long merchantId, @Param("statuses") List<OrderStatus> statuses);

    @Select("<script>SELECT COUNT(*) FROM orders WHERE paid_at &gt;= #{startAt} AND paid_at &lt;= #{endAt}</script>")
    long countPaidOrdersBetween(@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);

    @Select("<script>SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE paid_at &gt;= #{startAt} AND paid_at &lt;= #{endAt}</script>")
    BigDecimal sumPaidOrderAmountBetween(@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);

}
