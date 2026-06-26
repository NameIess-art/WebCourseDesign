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
public interface CouponMapper {

    @Select("SELECT * FROM coupons WHERE id = #{id}")
    @Results(id = "CouponMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "thresholdAmount", column = "threshold_amount"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "active", column = "active"),
        @Result(property = "validUntil", column = "valid_until")
    })
    Optional<Coupon> findById(@Param("id") Long id);

    @Select("SELECT * FROM coupons")
    @ResultMap("CouponMap")
    List<Coupon> findAll();

    @Select("SELECT * FROM coupons LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("CouponMap")
    List<Coupon> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM coupons")
    long count();

    @Insert("INSERT INTO coupons (name, threshold_amount, discount_amount, stock, active, valid_until) VALUES (#{name}, #{thresholdAmount}, #{discountAmount}, #{stock}, #{active}, #{validUntil})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon entity);

    @Update("UPDATE coupons SET name = #{name}, threshold_amount = #{thresholdAmount}, discount_amount = #{discountAmount}, stock = #{stock}, active = #{active}, valid_until = #{validUntil} WHERE id = #{id}")
    int update(Coupon entity);

    @Delete("DELETE FROM coupons WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default Coupon save(Coupon entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<Coupon> entities) {
        for (Coupon entity : entities) {
            save(entity);
        }
    }

    default void delete(Coupon entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<Coupon> entities) {
        for (Coupon entity : entities) {
            delete(entity);
        }
    }

    default Page<Coupon> findAll(Pageable pageable) {
        List<Coupon> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM coupons WHERE active = true ORDER BY id desc</script>")
    @ResultMap("CouponMap")
    List<Coupon> findByActiveTrueOrderByIdDesc();

    @Select("<script>SELECT * FROM coupons WHERE active = true ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("CouponMap")
    List<Coupon> findByActiveTrueOrderByIdDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM coupons WHERE active = true</script>")
    long countFindByActiveTrueOrderByIdDesc();

    default Page<Coupon> findByActiveTrueOrderByIdDesc(Pageable pageable) {
        List<Coupon> content = findByActiveTrueOrderByIdDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueOrderByIdDesc());
    }

    @Update("UPDATE coupons SET stock = stock - 1 WHERE id = #{couponId} AND stock >= 0")
    int decreaseStock(@Param("couponId") Long couponId);

}
