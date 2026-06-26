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
public interface ProductQuestionMapper {

    @Select("SELECT * FROM product_questions WHERE id = #{id}")
    @Results(id = "ProductQuestionMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "question", column = "question"),
        @Result(property = "answer", column = "answer"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<ProductQuestion> findById(@Param("id") Long id);

    @Select("SELECT * FROM product_questions")
    @ResultMap("ProductQuestionMap")
    List<ProductQuestion> findAll();

    @Select("SELECT * FROM product_questions LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ProductQuestionMap")
    List<ProductQuestion> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM product_questions")
    long count();

    @Insert("INSERT INTO product_questions (product_id, user_id, question, answer, created_at) VALUES (#{product.id}, #{user.id}, #{question}, #{answer}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductQuestion entity);

    @Update("UPDATE product_questions SET product_id = #{product.id}, user_id = #{user.id}, question = #{question}, answer = #{answer}, created_at = #{createdAt} WHERE id = #{id}")
    int update(ProductQuestion entity);

    @Delete("DELETE FROM product_questions WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ProductQuestion save(ProductQuestion entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ProductQuestion> entities) {
        for (ProductQuestion entity : entities) {
            save(entity);
        }
    }

    default void delete(ProductQuestion entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ProductQuestion> entities) {
        for (ProductQuestion entity : entities) {
            delete(entity);
        }
    }

    default Page<ProductQuestion> findAll(Pageable pageable) {
        List<ProductQuestion> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM product_questions WHERE product_id = #{productId} ORDER BY created_at desc</script>")
    @ResultMap("ProductQuestionMap")
    List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    @Select("<script>SELECT * FROM product_questions WHERE product_id = #{productId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductQuestionMap")
    List<ProductQuestion> findByProductIdOrderByCreatedAtDescPage(@Param("productId") Long productId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM product_questions WHERE product_id = #{productId}</script>")
    long countFindByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    default Page<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable) {
        List<ProductQuestion> content = findByProductIdOrderByCreatedAtDescPage(productId, pageable);
        return new PageImpl<>(content, pageable, countFindByProductIdOrderByCreatedAtDesc(productId));
    }

}
