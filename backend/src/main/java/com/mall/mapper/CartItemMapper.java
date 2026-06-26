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
public interface CartItemMapper {

    @Select("SELECT * FROM cart_items WHERE id = #{id}")
    @Results(id = "CartItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "sku", column = "sku_id", one = @One(select = "com.mall.mapper.ProductSkuMapper.findById")),
        @Result(property = "quantity", column = "quantity")
    })
    Optional<CartItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM cart_items")
    @ResultMap("CartItemMap")
    List<CartItem> findAll();

    @Select("SELECT * FROM cart_items LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("CartItemMap")
    List<CartItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM cart_items")
    long count();

    @Insert("INSERT INTO cart_items (user_id, product_id, sku_id, quantity) VALUES (#{user.id}, #{product.id}, #{sku.id}, #{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem entity);

    @Update("UPDATE cart_items SET user_id = #{user.id}, product_id = #{product.id}, sku_id = #{sku.id}, quantity = #{quantity} WHERE id = #{id}")
    int update(CartItem entity);

    @Delete("DELETE FROM cart_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default CartItem save(CartItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<CartItem> entities) {
        for (CartItem entity : entities) {
            save(entity);
        }
    }

    default void delete(CartItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<CartItem> entities) {
        for (CartItem entity : entities) {
            delete(entity);
        }
    }

    default Page<CartItem> findAll(Pageable pageable) {
        List<CartItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM cart_items WHERE user_id = #{userId}</script>")
    @ResultMap("CartItemMap")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM cart_items WHERE user_id = #{user.id} AND product_id = #{product.id}</script>")
    @ResultMap("CartItemMap")
    Optional<CartItem> findByUserAndProduct(@Param("user") UserAccount user, @Param("product") Product product);

    @Select("<script>SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId} <if test='skuId != null'>AND sku_id = #{skuId}</if><if test='skuId == null'>AND sku_id IS NULL</if></script>")
    @ResultMap("CartItemMap")
    Optional<CartItem> findByUserIdAndProductIdAndSkuId(@Param("userId") Long userId, @Param("productId") Long productId, @Param("skuId") Long skuId);

    @Delete("<script>DELETE FROM cart_items WHERE user_id = #{userId}</script>")
    int deleteByUserId(@Param("userId") Long userId);

}
