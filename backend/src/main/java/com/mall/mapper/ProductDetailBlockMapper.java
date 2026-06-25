package com.mall.mapper;

import com.mall.entity.ProductDetailBlock;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ProductDetailBlockMapper extends MyBatisMapperSupport<ProductDetailBlock> {

    public ProductDetailBlockMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ProductDetailBlock.class);
    }

    public List<ProductDetailBlock> findByProductIdOrderBySortOrderAscIdAsc(Long productId) {
        return selectList("t.product_id = #{params.productId}", params("productId", productId), "t.sort_order asc, t.id asc");
    }

    public List<ProductDetailBlock> findByProductIdInOrderByProductIdAscSortOrderAscIdAsc(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        Map<String, Object> sqlParams = params();
        return selectList("t.product_id in " + inClause("productId", productIds, sqlParams), sqlParams, "t.product_id asc, t.sort_order asc, t.id asc");
    }
}
