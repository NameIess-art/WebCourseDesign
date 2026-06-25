package com.mall.mapper;

import com.mall.entity.CartItem;
import com.mall.entity.Product;
import com.mall.entity.UserAccount;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CartItemMapper extends MyBatisMapperSupport<CartItem> {

    public CartItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, CartItem.class);
    }

    public List<CartItem> findByUserId(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.id desc");
    }

    public Optional<CartItem> findByUserAndProduct(UserAccount user, Product product) {
        return Optional.ofNullable(selectOne("t.user_id = #{params.userId} and t.product_id = #{params.productId}", params("userId", user.getId(), "productId", product.getId())));
    }

    public Optional<CartItem> findByUserIdAndProductIdAndSkuId(Long userId, Long productId, Long skuId) {
        String skuSql = skuId == null ? "t.sku_id is null" : "t.sku_id = #{params.skuId}";
        return Optional.ofNullable(selectOne("t.user_id = #{params.userId} and t.product_id = #{params.productId} and " + skuSql,
                params("userId", userId, "productId", productId, "skuId", skuId)));
    }

    public int deleteByUserId(Long userId) {
        return executeUpdate("delete from cart_items where user_id = #{params.userId}", params("userId", userId));
    }
}
