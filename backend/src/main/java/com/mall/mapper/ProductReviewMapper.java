package com.mall.mapper;

import com.mall.entity.ProductReview;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductReviewMapper extends MyBatisMapperSupport<ProductReview> {

    public ProductReviewMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ProductReview.class);
    }

    public List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId) {
        return selectList("t.product_id = #{params.productId}", params("productId", productId), "t.created_at desc");
    }

    public Page<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable) {
        return selectPage("t.product_id = #{params.productId}", params("productId", productId), "t.created_at desc", pageable);
    }
}
