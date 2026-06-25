package com.mall.mapper;

import com.mall.entity.ProductSku;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ProductSkuMapper extends MyBatisMapperSupport<ProductSku> {

    public ProductSkuMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ProductSku.class);
    }

    public List<ProductSku> findByProductIdOrderByIdAsc(Long productId) {
        return selectList("t.product_id = #{params.productId}", params("productId", productId), "t.id asc");
    }

    public List<ProductSku> findByProductIdInOrderByProductIdAscIdAsc(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        Map<String, Object> sqlParams = params();
        return selectList("t.product_id in " + inClause("productId", productIds, sqlParams), sqlParams, "t.product_id asc, t.id asc");
    }

    public int decreaseStock(Long skuId, Integer quantity) {
        return executeUpdate("update product_skus set stock = stock - #{params.quantity} where id = #{params.skuId} and active = true and stock >= #{params.quantity}", params("skuId", skuId, "quantity", quantity));
    }

    public int restoreStock(Long skuId, Integer quantity) {
        return executeUpdate("update product_skus set stock = stock + #{params.quantity} where id = #{params.skuId}", params("skuId", skuId, "quantity", quantity));
    }
}
