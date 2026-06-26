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
public interface MerchantPenaltyMapper {

    @Select("SELECT * FROM merchant_penalties WHERE id = #{id}")
    @Results(id = "MerchantPenaltyMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "merchant", column = "merchant_id", one = @One(select = "com.mall.mapper.MerchantMapper.findById")),
        @Result(property = "penaltyType", column = "penalty_type"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<MerchantPenalty> findById(@Param("id") Long id);

    @Select("SELECT * FROM merchant_penalties")
    @ResultMap("MerchantPenaltyMap")
    List<MerchantPenalty> findAll();

    @Select("SELECT * FROM merchant_penalties LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("MerchantPenaltyMap")
    List<MerchantPenalty> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM merchant_penalties")
    long count();

    @Insert("INSERT INTO merchant_penalties (merchant_id, penalty_type, reason, created_at) VALUES (#{merchant.id}, #{penaltyType}, #{reason}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MerchantPenalty entity);

    @Update("UPDATE merchant_penalties SET merchant_id = #{merchant.id}, penalty_type = #{penaltyType}, reason = #{reason}, created_at = #{createdAt} WHERE id = #{id}")
    int update(MerchantPenalty entity);

    @Delete("DELETE FROM merchant_penalties WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default MerchantPenalty save(MerchantPenalty entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<MerchantPenalty> entities) {
        for (MerchantPenalty entity : entities) {
            save(entity);
        }
    }

    default void delete(MerchantPenalty entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<MerchantPenalty> entities) {
        for (MerchantPenalty entity : entities) {
            delete(entity);
        }
    }

    default Page<MerchantPenalty> findAll(Pageable pageable) {
        List<MerchantPenalty> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
