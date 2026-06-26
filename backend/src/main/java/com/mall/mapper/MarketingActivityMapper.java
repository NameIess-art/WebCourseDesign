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
public interface MarketingActivityMapper {

    @Select("SELECT * FROM marketing_activities WHERE id = #{id}")
    @Results(id = "MarketingActivityMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "title", column = "title"),
        @Result(property = "type", column = "type"),
        @Result(property = "ruleText", column = "rule_text"),
        @Result(property = "status", column = "status"),
        @Result(property = "startAt", column = "start_at"),
        @Result(property = "endAt", column = "end_at")
    })
    Optional<MarketingActivity> findById(@Param("id") Long id);

    @Select("SELECT * FROM marketing_activities")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findAll();

    @Select("SELECT * FROM marketing_activities LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM marketing_activities")
    long count();

    @Insert("INSERT INTO marketing_activities (title, type, rule_text, status, start_at, end_at) VALUES (#{title}, #{type}, #{ruleText}, #{status}, #{startAt}, #{endAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MarketingActivity entity);

    @Update("UPDATE marketing_activities SET title = #{title}, type = #{type}, rule_text = #{ruleText}, status = #{status}, start_at = #{startAt}, end_at = #{endAt} WHERE id = #{id}")
    int update(MarketingActivity entity);

    @Delete("DELETE FROM marketing_activities WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default MarketingActivity save(MarketingActivity entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<MarketingActivity> entities) {
        for (MarketingActivity entity : entities) {
            save(entity);
        }
    }

    default void delete(MarketingActivity entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<MarketingActivity> entities) {
        for (MarketingActivity entity : entities) {
            delete(entity);
        }
    }

    default Page<MarketingActivity> findAll(Pageable pageable) {
        List<MarketingActivity> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM marketing_activities ORDER BY start_at desc</script>")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findAllByOrderByStartAtDesc();

    @Select("<script>SELECT * FROM marketing_activities ORDER BY start_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findAllByOrderByStartAtDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM marketing_activities</script>")
    long countFindAllByOrderByStartAtDesc();

    default Page<MarketingActivity> findAllByOrderByStartAtDesc(Pageable pageable) {
        List<MarketingActivity> content = findAllByOrderByStartAtDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindAllByOrderByStartAtDesc());
    }

    @Select("<script>SELECT * FROM marketing_activities WHERE LOWER(status) = LOWER(#{status}) ORDER BY start_at DESC</script>")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(@Param("status") String status);

    @Select("<script>SELECT * FROM marketing_activities WHERE LOWER(status) = LOWER(#{status}) ORDER BY start_at DESC LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("MarketingActivityMap")
    List<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDescPage(@Param("status") String status, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM marketing_activities WHERE LOWER(status) = LOWER(#{status})</script>")
    long countFindByStatusIgnoreCaseOrderByStartAtDesc(@Param("status") String status);

    default Page<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(String status, Pageable pageable) {
        List<MarketingActivity> content = findByStatusIgnoreCaseOrderByStartAtDescPage(status, pageable);
        return new PageImpl<>(content, pageable, countFindByStatusIgnoreCaseOrderByStartAtDesc(status));
    }

}
