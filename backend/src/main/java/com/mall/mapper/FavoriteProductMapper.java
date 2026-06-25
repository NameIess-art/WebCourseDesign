package com.mall.mapper;

import com.mall.entity.FavoriteProduct;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FavoriteProductMapper extends MyBatisMapperSupport<FavoriteProduct> {

    public FavoriteProductMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, FavoriteProduct.class);
    }

    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return countWhere("t.user_id = #{params.userId} and t.product_id = #{params.productId}", params("userId", userId, "productId", productId)) > 0;
    }

    public Optional<FavoriteProduct> findByUserIdAndProductId(Long userId, Long productId) {
        return Optional.ofNullable(selectOne("t.user_id = #{params.userId} and t.product_id = #{params.productId}", params("userId", userId, "productId", productId)));
    }

    public List<FavoriteProduct> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc");
    }

    public Page<FavoriteProduct> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc", pageable);
    }
}
