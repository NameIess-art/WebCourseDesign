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
public interface FavoriteProductMapper {

    @Select("SELECT * FROM favorite_products WHERE id = #{id}")
    @Results(id = "FavoriteProductMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<FavoriteProduct> findById(@Param("id") Long id);

    @Select("SELECT * FROM favorite_products")
    @ResultMap("FavoriteProductMap")
    List<FavoriteProduct> findAll();

    @Select("SELECT * FROM favorite_products LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("FavoriteProductMap")
    List<FavoriteProduct> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM favorite_products")
    long count();

    @Insert("INSERT INTO favorite_products (user_id, product_id, created_at) VALUES (#{user.id}, #{product.id}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FavoriteProduct entity);

    @Update("UPDATE favorite_products SET user_id = #{user.id}, product_id = #{product.id}, created_at = #{createdAt} WHERE id = #{id}")
    int update(FavoriteProduct entity);

    @Delete("DELETE FROM favorite_products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default FavoriteProduct save(FavoriteProduct entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<FavoriteProduct> entities) {
        for (FavoriteProduct entity : entities) {
            save(entity);
        }
    }

    default void delete(FavoriteProduct entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<FavoriteProduct> entities) {
        for (FavoriteProduct entity : entities) {
            delete(entity);
        }
    }

    default Page<FavoriteProduct> findAll(Pageable pageable) {
        List<FavoriteProduct> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT COUNT(*) FROM favorite_products WHERE user_id = #{userId} AND product_id = #{productId}</script>")
    long countForExistsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    default boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return countForExistsByUserIdAndProductId(userId, productId) > 0;
    }

    @Select("<script>SELECT * FROM favorite_products</script>")
    @ResultMap("FavoriteProductMap")
    Optional<FavoriteProduct> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("<script>SELECT * FROM favorite_products WHERE user_id = #{userId} ORDER BY created_at desc</script>")
    @ResultMap("FavoriteProductMap")
    List<FavoriteProduct> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM favorite_products WHERE user_id = #{userId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("FavoriteProductMap")
    List<FavoriteProduct> findByUserIdOrderByCreatedAtDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM favorite_products WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    default Page<FavoriteProduct> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        List<FavoriteProduct> content = findByUserIdOrderByCreatedAtDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByCreatedAtDesc(userId));
    }

}
