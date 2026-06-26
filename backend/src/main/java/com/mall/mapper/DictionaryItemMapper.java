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
public interface DictionaryItemMapper {

    @Select("SELECT * FROM dictionaries WHERE id = #{id}")
    @Results(id = "DictionaryItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "dictType", column = "dict_type"),
        @Result(property = "dictKey", column = "dict_key"),
        @Result(property = "dictValue", column = "dict_value")
    })
    Optional<DictionaryItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM dictionaries")
    @ResultMap("DictionaryItemMap")
    List<DictionaryItem> findAll();

    @Select("SELECT * FROM dictionaries LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("DictionaryItemMap")
    List<DictionaryItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM dictionaries")
    long count();

    @Insert("INSERT INTO dictionaries (dict_type, dict_key, dict_value) VALUES (#{dictType}, #{dictKey}, #{dictValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DictionaryItem entity);

    @Update("UPDATE dictionaries SET dict_type = #{dictType}, dict_key = #{dictKey}, dict_value = #{dictValue} WHERE id = #{id}")
    int update(DictionaryItem entity);

    @Delete("DELETE FROM dictionaries WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default DictionaryItem save(DictionaryItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<DictionaryItem> entities) {
        for (DictionaryItem entity : entities) {
            save(entity);
        }
    }

    default void delete(DictionaryItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<DictionaryItem> entities) {
        for (DictionaryItem entity : entities) {
            delete(entity);
        }
    }

    default Page<DictionaryItem> findAll(Pageable pageable) {
        List<DictionaryItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
