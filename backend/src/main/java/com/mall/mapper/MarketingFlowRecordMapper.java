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
public interface MarketingFlowRecordMapper {

    @Select("SELECT * FROM marketing_flow_records WHERE id = #{id}")
    @Results(id = "MarketingFlowRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "flowType", column = "flow_type"),
        @Result(property = "title", column = "title"),
        @Result(property = "amount", column = "amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "description", column = "description"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<MarketingFlowRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM marketing_flow_records")
    @ResultMap("MarketingFlowRecordMap")
    List<MarketingFlowRecord> findAll();

    @Select("SELECT * FROM marketing_flow_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("MarketingFlowRecordMap")
    List<MarketingFlowRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM marketing_flow_records")
    long count();

    @Insert("INSERT INTO marketing_flow_records (flow_type, title, amount, status, description, created_at) VALUES (#{flowType}, #{title}, #{amount}, #{status}, #{description}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MarketingFlowRecord entity);

    @Update("UPDATE marketing_flow_records SET flow_type = #{flowType}, title = #{title}, amount = #{amount}, status = #{status}, description = #{description}, created_at = #{createdAt} WHERE id = #{id}")
    int update(MarketingFlowRecord entity);

    @Delete("DELETE FROM marketing_flow_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default MarketingFlowRecord save(MarketingFlowRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<MarketingFlowRecord> entities) {
        for (MarketingFlowRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(MarketingFlowRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<MarketingFlowRecord> entities) {
        for (MarketingFlowRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<MarketingFlowRecord> findAll(Pageable pageable) {
        List<MarketingFlowRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
