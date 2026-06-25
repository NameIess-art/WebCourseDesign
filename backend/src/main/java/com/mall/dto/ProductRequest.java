package com.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

// 商品新增/编辑的统一请求体：商家后台提交后由控制器校验，再交给商品管理服务保存。
public record ProductRequest(
        // 商品主表字段，使用非空和数值下限注解保护接口边界。
        @NotBlank String name,
        @NotBlank String subtitle,
        @NotBlank String description,
        @NotBlank String imageUrl,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        // 分类只传编号，服务层会查询分类实体并建立关联关系。
        @NotNull Long categoryId,
        @NotNull Boolean active,
        // 以下字段为空时，服务层会补充默认编码、规格和促销标签。
        String skuCode,
        String spec,
        String promotionTag,
        // 规格列表和详情段落属于商品子表，服务层在同一事务内同步。
        List<ProductSkuRequest> skus,
        List<ProductDetailBlockRequest> detailBlocks
) {
}
