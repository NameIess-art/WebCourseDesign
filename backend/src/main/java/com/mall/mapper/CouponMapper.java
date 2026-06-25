package com.mall.mapper;

import com.mall.entity.Coupon;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponMapper extends MyBatisMapperSupport<Coupon> {

    public CouponMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, Coupon.class);
    }

    public List<Coupon> findByActiveTrueOrderByIdDesc() {
        return selectList("t.active = true", params(), "t.id desc");
    }

    public Page<Coupon> findByActiveTrueOrderByIdDesc(Pageable pageable) {
        return selectPage("t.active = true", params(), "t.id desc", pageable);
    }

    public int decreaseStock(Long couponId) {
        return executeUpdate("update coupons set stock = stock - 1 where id = #{params.couponId} and active = true and stock > 0", params("couponId", couponId));
    }
}
