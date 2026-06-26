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
public interface ProductMapper {

    @Select("SELECT * FROM products WHERE id = #{id}")
    @Results(id = "ProductMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "subtitle", column = "subtitle"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "active", column = "active"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "spec", column = "spec"),
        @Result(property = "promotionTag", column = "promotion_tag"),
        @Result(property = "favoriteCount", column = "favorite_count"),
        @Result(property = "questionCount", column = "question_count"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "version", column = "version"),
        @Result(property = "category", column = "category_id", one = @One(select = "com.mall.mapper.CategoryMapper.findById")),
        @Result(property = "merchant", column = "merchant_id", one = @One(select = "com.mall.mapper.MerchantMapper.findById"))
    })
    Optional<Product> findById(@Param("id") Long id);

    @Select("SELECT * FROM products")
    @ResultMap("ProductMap")
    List<Product> findAll();

    @Select("SELECT * FROM products LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ProductMap")
    List<Product> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM products")
    long count();

    @Insert("INSERT INTO products (name, subtitle, description, image_url, price, stock, sales, active, sku_code, spec, promotion_tag, favorite_count, question_count, rating, version, category_id, merchant_id) VALUES (#{name}, #{subtitle}, #{description}, #{imageUrl}, #{price}, #{stock}, #{sales}, #{active}, #{skuCode}, #{spec}, #{promotionTag}, #{favoriteCount}, #{questionCount}, #{rating}, #{version}, #{category.id}, #{merchant.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product entity);

    @Update("UPDATE products SET name = #{name}, subtitle = #{subtitle}, description = #{description}, image_url = #{imageUrl}, price = #{price}, stock = #{stock}, sales = #{sales}, active = #{active}, sku_code = #{skuCode}, spec = #{spec}, promotion_tag = #{promotionTag}, favorite_count = #{favoriteCount}, question_count = #{questionCount}, rating = #{rating}, version = #{version}, category_id = #{category.id}, merchant_id = #{merchant.id} WHERE id = #{id}")
    int update(Product entity);

    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default Product save(Product entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<Product> entities) {
        for (Product entity : entities) {
            save(entity);
        }
    }

    default void delete(Product entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<Product> entities) {
        for (Product entity : entities) {
            delete(entity);
        }
    }

    default Page<Product> findAll(Pageable pageable) {
        List<Product> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM products WHERE active = true ORDER BY id desc</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueOrderByIdDesc();

    @Select("<script>SELECT * FROM products WHERE active = true and lower(name) like lower(concat('%', #{keyword}, '%')) ORDER BY id desc</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(@Param("keyword") String keyword);

    @Select("<script>SELECT * FROM products WHERE active = true and category_id = #{categoryId} ORDER BY id desc</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndCategoryIdOrderByIdDesc(@Param("categoryId") Long categoryId);

    @Select("<script>SELECT * FROM products WHERE active = true and category_id = #{categoryId} and lower(name) like lower(concat('%', #{keyword}, '%')) ORDER BY id desc</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Select("<script>SELECT * FROM products WHERE active = true ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueOrderByIdDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products WHERE active = true</script>")
    long countFindByActiveTrueOrderByIdDesc();

    default Page<Product> findByActiveTrueOrderByIdDesc(Pageable pageable) {
        List<Product> content = findByActiveTrueOrderByIdDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueOrderByIdDesc());
    }

    @Select("<script>SELECT * FROM products WHERE active = true and category_id = #{categoryId} ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndCategoryIdOrderByIdDescPage(@Param("categoryId") Long categoryId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products WHERE active = true and category_id = #{categoryId}</script>")
    long countFindByActiveTrueAndCategoryIdOrderByIdDesc(@Param("categoryId") Long categoryId);

    default Page<Product> findByActiveTrueAndCategoryIdOrderByIdDesc(Long categoryId, Pageable pageable) {
        List<Product> content = findByActiveTrueAndCategoryIdOrderByIdDescPage(categoryId, pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueAndCategoryIdOrderByIdDesc(categoryId));
    }

    @Select("<script>SELECT * FROM products WHERE active = true and lower(name) like lower(concat('%', #{keyword}, '%')) ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDescPage(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products WHERE active = true and lower(name) like lower(concat('%', #{keyword}, '%'))</script>")
    long countFindByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(@Param("keyword") String keyword);

    default Page<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(String keyword, Pageable pageable) {
        List<Product> content = findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDescPage(keyword, pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(keyword));
    }

    @Select("<script>SELECT * FROM products WHERE active = true and category_id = #{categoryId} and lower(name) like lower(concat('%', #{keyword}, '%')) ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDescPage(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products WHERE active = true and category_id = #{categoryId} and lower(name) like lower(concat('%', #{keyword}, '%'))</script>")
    long countFindByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    default Page<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(Long categoryId, String keyword, Pageable pageable) {
        List<Product> content = findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDescPage(categoryId, keyword, pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(categoryId, keyword));
    }

    @Select("<script>SELECT * FROM products ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findAllByOrderByIdDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products</script>")
    long countFindAllByOrderByIdDesc();

    default Page<Product> findAllByOrderByIdDesc(Pageable pageable) {
        List<Product> content = findAllByOrderByIdDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindAllByOrderByIdDesc());
    }

    @Select("<script>SELECT * FROM products WHERE merchant_id = #{merchantId} ORDER BY id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("ProductMap")
    List<Product> findByMerchantIdOrderByIdDescPage(@Param("merchantId") Long merchantId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM products WHERE merchant_id = #{merchantId}</script>")
    long countFindByMerchantIdOrderByIdDesc(@Param("merchantId") Long merchantId);

    default Page<Product> findByMerchantIdOrderByIdDesc(Long merchantId, Pageable pageable) {
        List<Product> content = findByMerchantIdOrderByIdDescPage(merchantId, pageable);
        return new PageImpl<>(content, pageable, countFindByMerchantIdOrderByIdDesc(merchantId));
    }

    @Select("<script>SELECT COUNT(*) FROM products WHERE stock &lt;= #{stock}</script>")
    long countByStockLessThanEqual(@Param("stock") Integer stock);

    @Select("<script>SELECT COUNT(*) FROM products WHERE merchant_id = #{merchantId}</script>")
    long countByMerchantId(@Param("merchantId") Long merchantId);

    @Update("UPDATE products SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE products SET stock = stock + #{quantity} WHERE id = #{productId}")
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

}
