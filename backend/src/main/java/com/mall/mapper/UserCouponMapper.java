package com.mall.mapper;

import com.mall.entity.UserCoupon;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCouponMapper extends MyBatisMapperSupport<UserCoupon> {

    public UserCouponMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, UserCoupon.class);
    }

    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return countWhere("t.user_id = #{params.userId} and t.coupon_id = #{params.couponId}", params("userId", userId, "couponId", couponId)) > 0;
    }

    public List<UserCoupon> findByUserIdOrderByClaimedAtDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.claimed_at desc");
    }
}
