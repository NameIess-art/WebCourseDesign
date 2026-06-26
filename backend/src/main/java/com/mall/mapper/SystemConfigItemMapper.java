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
public interface SystemConfigItemMapper {

    @Select("SELECT * FROM system_config_items WHERE id = #{id}")
    @Results(id = "SystemConfigItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "configKey", column = "config_key"),
        @Result(property = "configValue", column = "config_value"),
        @Result(property = "description", column = "description")
    })
    Optional<SystemConfigItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM system_config_items")
    @ResultMap("SystemConfigItemMap")
    List<SystemConfigItem> findAll();

    @Select("SELECT * FROM system_config_items LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("SystemConfigItemMap")
    List<SystemConfigItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM system_config_items")
    long count();

    @Insert("INSERT INTO system_config_items (config_key, config_value, description) VALUES (#{configKey}, #{configValue}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfigItem entity);

    @Update("UPDATE system_config_items SET config_key = #{configKey}, config_value = #{configValue}, description = #{description} WHERE id = #{id}")
    int update(SystemConfigItem entity);

    @Delete("DELETE FROM system_config_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default SystemConfigItem save(SystemConfigItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<SystemConfigItem> entities) {
        for (SystemConfigItem entity : entities) {
            save(entity);
        }
    }

    default void delete(SystemConfigItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<SystemConfigItem> entities) {
        for (SystemConfigItem entity : entities) {
            delete(entity);
        }
    }

    default Page<SystemConfigItem> findAll(Pageable pageable) {
        List<SystemConfigItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM system_config_items</script>")
    @ResultMap("SystemConfigItemMap")
    Optional<SystemConfigItem> findByConfigKey(@Param("configKey") String configKey);

}
