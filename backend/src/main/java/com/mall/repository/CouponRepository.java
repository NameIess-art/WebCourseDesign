package com.mall.repository;

import com.mall.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByActiveTrueOrderByIdDesc();
    Page<Coupon> findByActiveTrueOrderByIdDesc(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Coupon c set c.stock = c.stock - 1 where c.id = :couponId and c.active = true and c.stock > 0")
    int decreaseStock(@Param("couponId") Long couponId);
}
