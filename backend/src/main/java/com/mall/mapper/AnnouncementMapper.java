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
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcements WHERE id = #{id}")
    @Results(id = "AnnouncementMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "title", column = "title"),
        @Result(property = "content", column = "content"),
        @Result(property = "popup", column = "popup"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<Announcement> findById(@Param("id") Long id);

    @Select("SELECT * FROM announcements")
    @ResultMap("AnnouncementMap")
    List<Announcement> findAll();

    @Select("SELECT * FROM announcements LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("AnnouncementMap")
    List<Announcement> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM announcements")
    long count();

    @Insert("INSERT INTO announcements (title, content, popup, created_at) VALUES (#{title}, #{content}, #{popup}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement entity);

    @Update("UPDATE announcements SET title = #{title}, content = #{content}, popup = #{popup}, created_at = #{createdAt} WHERE id = #{id}")
    int update(Announcement entity);

    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default Announcement save(Announcement entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<Announcement> entities) {
        for (Announcement entity : entities) {
            save(entity);
        }
    }

    default void delete(Announcement entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<Announcement> entities) {
        for (Announcement entity : entities) {
            delete(entity);
        }
    }

    default Page<Announcement> findAll(Pageable pageable) {
        List<Announcement> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
