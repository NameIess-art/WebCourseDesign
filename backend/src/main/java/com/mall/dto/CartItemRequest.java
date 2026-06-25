package com.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 加入购物车请求体：商品详情页点击加购时发送。
public record CartItemRequest(
        // 商品编号必填，服务层会校验商品是否存在、是否上架。
        @NotNull Long productId,
        // 规格编号可选，不传时后端会解析商品默认规格。
        Long skuId,
        // 数量至少为 1，库存是否充足由服务层结合数据库再次校验。
        @NotNull @Min(1) Integer quantity
) {
}
