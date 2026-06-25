package com.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 结算请求体：购物车页提交订单时发送。
public record CheckoutRequest(
        // 收货地址必填，直接写入订单主表形成下单快照。
        @NotBlank @Size(max = 255) String shippingAddress,
        // 优惠券和积分是可选抵扣项，服务层会校验归属、门槛和可用余额。
        Long couponId,
        Integer pointsUsed,
        // 当前结算以购物车为准，该字段保留给扩展单规格直购场景。
        Long skuId,
        // 幂等键用于防止用户重复点击或网络重试导致重复生成订单。
        String idempotencyKey
) {
}
