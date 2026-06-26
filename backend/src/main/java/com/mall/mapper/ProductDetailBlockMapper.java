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
public interface ProductDetailBlockMapper {

    @Select("SELECT * FROM product_detail_blocks WHERE id = #{id}")
    @Results(id = "ProductDetailBlockMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "blockType", column = "block_type"),
        @Result(property = "content", column = "content"),
        @Result(property = "sortOrder", column = "sort_order")
    })
    Optional<ProductDetailBlock> findById(@Param("id") Long id);

    @Select("SELECT * FROM product_detail_blocks")
    @ResultMap("ProductDetailBlockMap")
    List<ProductDetailBlock> findAll();

    @Select("SELECT * FROM product_detail_blocks LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ProductDetailBlockMap")
    List<ProductDetailBlock> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM product_detail_blocks")
    long count();

    @Insert("INSERT INTO product_detail_blocks (product_id, block_type, content, sort_order) VALUES (#{product.id}, #{blockType}, #{content}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductDetailBlock entity);

    @Update("UPDATE product_detail_blocks SET product_id = #{product.id}, block_type = #{blockType}, content = #{content}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(ProductDetailBlock entity);

    @Delete("DELETE FROM product_detail_blocks WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ProductDetailBlock save(ProductDetailBlock entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ProductDetailBlock> entities) {
        for (ProductDetailBlock entity : entities) {
            save(entity);
        }
    }

    default void delete(ProductDetailBlock entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ProductDetailBlock> entities) {
        for (ProductDetailBlock entity : entities) {
            delete(entity);
        }
    }

    default Page<ProductDetailBlock> findAll(Pageable pageable) {
        List<ProductDetailBlock> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM product_detail_blocks WHERE product_id = #{productId} ORDER BY sort_order asc, id asc</script>")
    @ResultMap("ProductDetailBlockMap")
    List<ProductDetailBlock> findByProductIdOrderBySortOrderAscIdAsc(@Param("productId") Long productId);

    @Select("<script>SELECT * FROM product_detail_blocks</script>")
    @ResultMap("ProductDetailBlockMap")
    List<ProductDetailBlock> findByProductIdInOrderByProductIdAscSortOrderAscIdAsc(@Param("productIds") Collection<Long> productIds);

}
