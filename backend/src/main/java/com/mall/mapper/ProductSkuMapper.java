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
public interface ProductSkuMapper {

    @Select("SELECT * FROM product_skus WHERE id = #{id}")
    @Results(id = "ProductSkuMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "specName", column = "spec_name"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "active", column = "active")
    })
    Optional<ProductSku> findById(@Param("id") Long id);

    @Select("SELECT * FROM product_skus")
    @ResultMap("ProductSkuMap")
    List<ProductSku> findAll();

    @Select("SELECT * FROM product_skus LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ProductSkuMap")
    List<ProductSku> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM product_skus")
    long count();

    @Insert("INSERT INTO product_skus (product_id, sku_code, spec_name, price, stock, active) VALUES (#{product.id}, #{skuCode}, #{specName}, #{price}, #{stock}, #{active})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSku entity);

    @Update("UPDATE product_skus SET product_id = #{product.id}, sku_code = #{skuCode}, spec_name = #{specName}, price = #{price}, stock = #{stock}, active = #{active} WHERE id = #{id}")
    int update(ProductSku entity);

    @Delete("DELETE FROM product_skus WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ProductSku save(ProductSku entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ProductSku> entities) {
        for (ProductSku entity : entities) {
            save(entity);
        }
    }

    default void delete(ProductSku entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ProductSku> entities) {
        for (ProductSku entity : entities) {
            delete(entity);
        }
    }

    default Page<ProductSku> findAll(Pageable pageable) {
        List<ProductSku> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM product_skus WHERE product_id = #{productId} ORDER BY id asc</script>")
    @ResultMap("ProductSkuMap")
    List<ProductSku> findByProductIdOrderByIdAsc(@Param("productId") Long productId);

    @Select("<script>SELECT * FROM product_skus WHERE product_id IN <foreach collection='productIds' item='productId' open='(' separator=',' close=')'>#{productId}</foreach> ORDER BY product_id asc, id asc</script>")
    @ResultMap("ProductSkuMap")
    List<ProductSku> findByProductIdInOrderByProductIdAscIdAsc(@Param("productIds") Collection<Long> productIds);

    @Update("UPDATE product_skus SET stock = stock - #{quantity} WHERE id = #{skuId} AND stock >= #{quantity}")
    int decreaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Update("UPDATE product_skus SET stock = stock + #{quantity} WHERE id = #{skuId}")
    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

}
