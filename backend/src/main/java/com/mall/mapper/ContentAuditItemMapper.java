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
public interface ContentAuditItemMapper {

    @Select("SELECT * FROM content_audit_items WHERE id = #{id}")
    @Results(id = "ContentAuditItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "contentType", column = "content_type"),
        @Result(property = "target", column = "target"),
        @Result(property = "content", column = "content"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<ContentAuditItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM content_audit_items")
    @ResultMap("ContentAuditItemMap")
    List<ContentAuditItem> findAll();

    @Select("SELECT * FROM content_audit_items LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ContentAuditItemMap")
    List<ContentAuditItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM content_audit_items")
    long count();

    @Insert("INSERT INTO content_audit_items (content_type, target, content, status, created_at) VALUES (#{contentType}, #{target}, #{content}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContentAuditItem entity);

    @Update("UPDATE content_audit_items SET content_type = #{contentType}, target = #{target}, content = #{content}, status = #{status}, created_at = #{createdAt} WHERE id = #{id}")
    int update(ContentAuditItem entity);

    @Delete("DELETE FROM content_audit_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ContentAuditItem save(ContentAuditItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ContentAuditItem> entities) {
        for (ContentAuditItem entity : entities) {
            save(entity);
        }
    }

    default void delete(ContentAuditItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ContentAuditItem> entities) {
        for (ContentAuditItem entity : entities) {
            delete(entity);
        }
    }

    default Page<ContentAuditItem> findAll(Pageable pageable) {
        List<ContentAuditItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
