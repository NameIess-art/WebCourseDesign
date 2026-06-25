package com.mall.mapper;

import com.mall.entity.ProductQuestion;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductQuestionMapper extends MyBatisMapperSupport<ProductQuestion> {

    public ProductQuestionMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ProductQuestion.class);
    }

    public List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId) {
        return selectList("t.product_id = #{params.productId}", params("productId", productId), "t.created_at desc");
    }

    public Page<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable) {
        return selectPage("t.product_id = #{params.productId}", params("productId", productId), "t.created_at desc", pageable);
    }
}
