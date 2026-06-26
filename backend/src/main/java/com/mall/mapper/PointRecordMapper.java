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
public interface PointRecordMapper {

    @Select("SELECT * FROM point_records WHERE id = #{id}")
    @Results(id = "PointRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "points", column = "points"),
        @Result(property = "type", column = "type"),
        @Result(property = "description", column = "description"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<PointRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM point_records")
    @ResultMap("PointRecordMap")
    List<PointRecord> findAll();

    @Select("SELECT * FROM point_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("PointRecordMap")
    List<PointRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM point_records")
    long count();

    @Insert("INSERT INTO point_records (user_id, points, type, description, created_at) VALUES (#{user.id}, #{points}, #{type}, #{description}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointRecord entity);

    @Update("UPDATE point_records SET user_id = #{user.id}, points = #{points}, type = #{type}, description = #{description}, created_at = #{createdAt} WHERE id = #{id}")
    int update(PointRecord entity);

    @Delete("DELETE FROM point_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default PointRecord save(PointRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<PointRecord> entities) {
        for (PointRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(PointRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<PointRecord> entities) {
        for (PointRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<PointRecord> findAll(Pageable pageable) {
        List<PointRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM point_records WHERE user_id = #{userId} ORDER BY created_at desc</script>")
    @ResultMap("PointRecordMap")
    List<PointRecord> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM point_records WHERE user_id = #{userId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("PointRecordMap")
    List<PointRecord> findByUserIdOrderByCreatedAtDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM point_records WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    default Page<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        List<PointRecord> content = findByUserIdOrderByCreatedAtDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByCreatedAtDesc(userId));
    }

}
