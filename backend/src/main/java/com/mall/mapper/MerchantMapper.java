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
public interface MerchantMapper {

    @Select("SELECT * FROM merchants WHERE id = #{id}")
    @Results(id = "MerchantMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "contactPhone", column = "contact_phone"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<Merchant> findById(@Param("id") Long id);

    @Select("SELECT * FROM merchants")
    @ResultMap("MerchantMap")
    List<Merchant> findAll();

    @Select("SELECT * FROM merchants LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("MerchantMap")
    List<Merchant> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM merchants")
    long count();

    @Insert("INSERT INTO merchants (name, contact_phone, status, created_at) VALUES (#{name}, #{contactPhone}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Merchant entity);

    @Update("UPDATE merchants SET name = #{name}, contact_phone = #{contactPhone}, status = #{status}, created_at = #{createdAt} WHERE id = #{id}")
    int update(Merchant entity);

    @Delete("DELETE FROM merchants WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default Merchant save(Merchant entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<Merchant> entities) {
        for (Merchant entity : entities) {
            save(entity);
        }
    }

    default void delete(Merchant entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<Merchant> entities) {
        for (Merchant entity : entities) {
            delete(entity);
        }
    }

    default Page<Merchant> findAll(Pageable pageable) {
        List<Merchant> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
