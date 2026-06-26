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
public interface ProductReviewMapper {

    @Select("SELECT * FROM product_reviews WHERE id = #{id}")
    @Results(id = "ProductReviewMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<ProductReview> findById(@Param("id") Long id);

    @Select("SELECT * FROM product_reviews")
    @ResultMap("ProductReviewMap")
    List<ProductReview> findAll();

    @Select("SELECT * FROM product_reviews LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ProductReviewMap")
    List<ProductReview> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM product_reviews")
    long count();

    @Insert("INSERT INTO product_reviews (product_id, user_id, rating, content, created_at) VALUES (#{product.id}, #{user.id}, #{rating}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductReview entity);

    @Update("UPDATE product_reviews SET product_id = #{product.id}, user_id = #{user.id}, rating = #{rating}, content = #{content}, created_at = #{createdAt} WHERE id = #{id}")
    int update(ProductReview entity);

    @Delete("DELETE FROM product_reviews WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ProductReview save(ProductReview entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ProductReview> entities) {
        for (ProductReview entity : entities) {
            save(entity);
        }
    }

    default void delete(ProductReview entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ProductReview> entities) {
        for (ProductReview entity : entities) {
            delete(entity);
        }
    }

    default Page<ProductReview> findAll(Pageable pageable) {
        List<ProductReview> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM product_reviews WHERE product_id = #{productId} ORDER BY created_at desc</script>")
    @ResultMap("ProductReviewMap")
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    @Select("<script>SELECT * FROM product_reviews WHERE product_id = #{productId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductReviewMap")
    List<ProductReview> findByProductIdOrderByCreatedAtDescPage(@Param("productId") Long productId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM product_reviews WHERE product_id = #{productId}</script>")
    long countFindByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    default Page<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable) {
        List<ProductReview> content = findByProductIdOrderByCreatedAtDescPage(productId, pageable);
        return new PageImpl<>(content, pageable, countFindByProductIdOrderByCreatedAtDesc(productId));
    }

}
