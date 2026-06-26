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
public interface CategoryMapper {

    @Select("SELECT * FROM categories WHERE id = #{id}")
    @Results(id = "CategoryMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "sortOrder", column = "sort_order")
    })
    Optional<Category> findById(@Param("id") Long id);

    @Select("SELECT * FROM categories")
    @ResultMap("CategoryMap")
    List<Category> findAll();

    @Select("SELECT * FROM categories LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("CategoryMap")
    List<Category> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM categories")
    long count();

    @Insert("INSERT INTO categories (name, sort_order) VALUES (#{name}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category entity);

    @Update("UPDATE categories SET name = #{name}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Category entity);

    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default Category save(Category entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<Category> entities) {
        for (Category entity : entities) {
            save(entity);
        }
    }

    default void delete(Category entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<Category> entities) {
        for (Category entity : entities) {
            delete(entity);
        }
    }

    default Page<Category> findAll(Pageable pageable) {
        List<Category> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM categories ORDER BY sort_order asc, id asc</script>")
    @ResultMap("CategoryMap")
    List<Category> findAllByOrderBySortOrderAscIdAsc();

    @Select("<script>SELECT * FROM categories ORDER BY sort_order asc, id asc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("CategoryMap")
    List<Category> findAllByOrderBySortOrderAscIdAscPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM categories</script>")
    long countFindAllByOrderBySortOrderAscIdAsc();

    default Page<Category> findAllByOrderBySortOrderAscIdAsc(Pageable pageable) {
        List<Category> content = findAllByOrderBySortOrderAscIdAscPage(pageable);
        return new PageImpl<>(content, pageable, countFindAllByOrderBySortOrderAscIdAsc());
    }

}
