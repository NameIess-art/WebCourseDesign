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
public interface PermissionItemMapper {

    @Select("SELECT * FROM permissions WHERE id = #{id}")
    @Results(id = "PermissionItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "code", column = "code"),
        @Result(property = "name", column = "name"),
        @Result(property = "permissionType", column = "permission_type")
    })
    Optional<PermissionItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM permissions")
    @ResultMap("PermissionItemMap")
    List<PermissionItem> findAll();

    @Select("SELECT * FROM permissions LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("PermissionItemMap")
    List<PermissionItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM permissions")
    long count();

    @Insert("INSERT INTO permissions (code, name, permission_type) VALUES (#{code}, #{name}, #{permissionType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PermissionItem entity);

    @Update("UPDATE permissions SET code = #{code}, name = #{name}, permission_type = #{permissionType} WHERE id = #{id}")
    int update(PermissionItem entity);

    @Delete("DELETE FROM permissions WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default PermissionItem save(PermissionItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<PermissionItem> entities) {
        for (PermissionItem entity : entities) {
            save(entity);
        }
    }

    default void delete(PermissionItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<PermissionItem> entities) {
        for (PermissionItem entity : entities) {
            delete(entity);
        }
    }

    default Page<PermissionItem> findAll(Pageable pageable) {
        List<PermissionItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
