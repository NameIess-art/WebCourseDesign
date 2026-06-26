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
public interface RoleDefinitionMapper {

    @Select("SELECT * FROM roles WHERE id = #{id}")
    @Results(id = "RoleDefinitionMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "code", column = "code"),
        @Result(property = "name", column = "name"),
        @Result(property = "menuPermissions", column = "menu_permissions"),
        @Result(property = "buttonPermissions", column = "button_permissions")
    })
    Optional<RoleDefinition> findById(@Param("id") Long id);

    @Select("SELECT * FROM roles")
    @ResultMap("RoleDefinitionMap")
    List<RoleDefinition> findAll();

    @Select("SELECT * FROM roles LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("RoleDefinitionMap")
    List<RoleDefinition> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM roles")
    long count();

    @Insert("INSERT INTO roles (code, name, menu_permissions, button_permissions) VALUES (#{code}, #{name}, #{menuPermissions}, #{buttonPermissions})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RoleDefinition entity);

    @Update("UPDATE roles SET code = #{code}, name = #{name}, menu_permissions = #{menuPermissions}, button_permissions = #{buttonPermissions} WHERE id = #{id}")
    int update(RoleDefinition entity);

    @Delete("DELETE FROM roles WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default RoleDefinition save(RoleDefinition entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<RoleDefinition> entities) {
        for (RoleDefinition entity : entities) {
            save(entity);
        }
    }

    default void delete(RoleDefinition entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<RoleDefinition> entities) {
        for (RoleDefinition entity : entities) {
            delete(entity);
        }
    }

    default Page<RoleDefinition> findAll(Pageable pageable) {
        List<RoleDefinition> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
