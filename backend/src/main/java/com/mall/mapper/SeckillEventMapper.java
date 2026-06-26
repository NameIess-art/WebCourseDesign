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
public interface SeckillEventMapper {

    @Select("SELECT * FROM seckill_events WHERE id = #{id}")
    @Results(id = "SeckillEventMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "product", column = "product_id", one = @One(select = "com.mall.mapper.ProductMapper.findById")),
        @Result(property = "seckillPrice", column = "seckill_price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sold", column = "sold"),
        @Result(property = "active", column = "active"),
        @Result(property = "startAt", column = "start_at"),
        @Result(property = "endAt", column = "end_at"),
        @Result(property = "version", column = "version")
    })
    Optional<SeckillEvent> findById(@Param("id") Long id);

    @Select("SELECT * FROM seckill_events")
    @ResultMap("SeckillEventMap")
    List<SeckillEvent> findAll();

    @Select("SELECT * FROM seckill_events LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("SeckillEventMap")
    List<SeckillEvent> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM seckill_events")
    long count();

    @Insert("INSERT INTO seckill_events (product_id, seckill_price, stock, sold, active, start_at, end_at, version) VALUES (#{product.id}, #{seckillPrice}, #{stock}, #{sold}, #{active}, #{startAt}, #{endAt}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SeckillEvent entity);

    @Update("UPDATE seckill_events SET product_id = #{product.id}, seckill_price = #{seckillPrice}, stock = #{stock}, sold = #{sold}, active = #{active}, start_at = #{startAt}, end_at = #{endAt}, version = #{version} WHERE id = #{id}")
    int update(SeckillEvent entity);

    @Delete("DELETE FROM seckill_events WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default SeckillEvent save(SeckillEvent entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<SeckillEvent> entities) {
        for (SeckillEvent entity : entities) {
            save(entity);
        }
    }

    default void delete(SeckillEvent entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<SeckillEvent> entities) {
        for (SeckillEvent entity : entities) {
            delete(entity);
        }
    }

    default Page<SeckillEvent> findAll(Pageable pageable) {
        List<SeckillEvent> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM seckill_events WHERE active = true ORDER BY start_at desc</script>")
    @ResultMap("SeckillEventMap")
    List<SeckillEvent> findByActiveTrueOrderByStartAtDesc();

    @Select("<script>SELECT * FROM seckill_events WHERE active = true ORDER BY start_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("SeckillEventMap")
    List<SeckillEvent> findByActiveTrueOrderByStartAtDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM seckill_events WHERE active = true</script>")
    long countFindByActiveTrueOrderByStartAtDesc();

    default Page<SeckillEvent> findByActiveTrueOrderByStartAtDesc(Pageable pageable) {
        List<SeckillEvent> content = findByActiveTrueOrderByStartAtDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindByActiveTrueOrderByStartAtDesc());
    }

    @Update("UPDATE seckill_events SET stock_count = stock_count - 1 WHERE id = #{eventId} AND stock_count >= 0")
    int decreaseStock(@Param("eventId") Long eventId);

}
