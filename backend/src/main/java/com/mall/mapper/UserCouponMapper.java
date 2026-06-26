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
public interface UserCouponMapper {

    @Select("SELECT * FROM user_coupons WHERE id = #{id}")
    @Results(id = "UserCouponMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "coupon", column = "coupon_id", one = @One(select = "com.mall.mapper.CouponMapper.findById")),
        @Result(property = "used", column = "used"),
        @Result(property = "claimedAt", column = "claimed_at")
    })
    Optional<UserCoupon> findById(@Param("id") Long id);

    @Select("SELECT * FROM user_coupons")
    @ResultMap("UserCouponMap")
    List<UserCoupon> findAll();

    @Select("SELECT * FROM user_coupons LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("UserCouponMap")
    List<UserCoupon> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM user_coupons")
    long count();

    @Insert("INSERT INTO user_coupons (user_id, coupon_id, used, claimed_at) VALUES (#{user.id}, #{coupon.id}, #{used}, #{claimedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon entity);

    @Update("UPDATE user_coupons SET user_id = #{user.id}, coupon_id = #{coupon.id}, used = #{used}, claimed_at = #{claimedAt} WHERE id = #{id}")
    int update(UserCoupon entity);

    @Delete("DELETE FROM user_coupons WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default UserCoupon save(UserCoupon entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<UserCoupon> entities) {
        for (UserCoupon entity : entities) {
            save(entity);
        }
    }

    default void delete(UserCoupon entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<UserCoupon> entities) {
        for (UserCoupon entity : entities) {
            delete(entity);
        }
    }

    default Page<UserCoupon> findAll(Pageable pageable) {
        List<UserCoupon> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT COUNT(*) FROM user_coupons WHERE user_id = #{userId} AND coupon_id = #{couponId}</script>")
    long countForExistsByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);

    default boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return countForExistsByUserIdAndCouponId(userId, couponId) > 0;
    }

    @Select("<script>SELECT * FROM user_coupons</script>")
    @ResultMap("UserCouponMap")
    List<UserCoupon> findByUserIdOrderByClaimedAtDesc(@Param("userId") Long userId);

}
