package com.mall.mapper;

import com.mall.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    @Results(id = "OrderItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "order", column = "order_id", one = @One(select = "com.mall.mapper.OrderMapper.findById")),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "sku", column = "sku_id", one = @One(select = "com.mall.mapper.ProductSkuMapper.findById")),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price")
    })
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO order_items (order_id, product_id, sku_id, quantity, price) VALUES (#{order.id}, #{product.id}, #{sku.id}, #{quantity}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);

    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);
}
