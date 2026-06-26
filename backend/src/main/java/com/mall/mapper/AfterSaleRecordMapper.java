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
public interface AfterSaleRecordMapper {

    @Select("SELECT * FROM after_sale_records WHERE id = #{id}")
    @Results(id = "AfterSaleRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "order", column = "order_id", one = @One(select = "com.mall.mapper.OrderMapper.findById")),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "type", column = "type"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<AfterSaleRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM after_sale_records")
    @ResultMap("AfterSaleRecordMap")
    List<AfterSaleRecord> findAll();

    @Select("SELECT * FROM after_sale_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("AfterSaleRecordMap")
    List<AfterSaleRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM after_sale_records")
    long count();

    @Insert("INSERT INTO after_sale_records (order_id, user_id, type, reason, status, created_at) VALUES (#{order.id}, #{user.id}, #{type}, #{reason}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AfterSaleRecord entity);

    @Update("UPDATE after_sale_records SET order_id = #{order.id}, user_id = #{user.id}, type = #{type}, reason = #{reason}, status = #{status}, created_at = #{createdAt} WHERE id = #{id}")
    int update(AfterSaleRecord entity);

    @Delete("DELETE FROM after_sale_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default AfterSaleRecord save(AfterSaleRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<AfterSaleRecord> entities) {
        for (AfterSaleRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(AfterSaleRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<AfterSaleRecord> entities) {
        for (AfterSaleRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<AfterSaleRecord> findAll(Pageable pageable) {
        List<AfterSaleRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM after_sale_records WHERE user_id = #{userId} ORDER BY created_at desc</script>")
    @ResultMap("AfterSaleRecordMap")
    List<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM after_sale_records WHERE user_id = #{userId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("AfterSaleRecordMap")
    List<AfterSaleRecord> findByUserIdOrderByCreatedAtDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM after_sale_records WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    default Page<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        List<AfterSaleRecord> content = findByUserIdOrderByCreatedAtDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByCreatedAtDesc(userId));
    }

}
