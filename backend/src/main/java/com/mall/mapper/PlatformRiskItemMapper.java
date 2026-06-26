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
public interface PlatformRiskItemMapper {

    @Select("SELECT * FROM platform_risk_items WHERE id = #{id}")
    @Results(id = "PlatformRiskItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "target", column = "target"),
        @Result(property = "riskType", column = "risk_type"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<PlatformRiskItem> findById(@Param("id") Long id);

    @Select("SELECT * FROM platform_risk_items")
    @ResultMap("PlatformRiskItemMap")
    List<PlatformRiskItem> findAll();

    @Select("SELECT * FROM platform_risk_items LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("PlatformRiskItemMap")
    List<PlatformRiskItem> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM platform_risk_items")
    long count();

    @Insert("INSERT INTO platform_risk_items (target, risk_type, description, status, created_at) VALUES (#{target}, #{riskType}, #{description}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlatformRiskItem entity);

    @Update("UPDATE platform_risk_items SET target = #{target}, risk_type = #{riskType}, description = #{description}, status = #{status}, created_at = #{createdAt} WHERE id = #{id}")
    int update(PlatformRiskItem entity);

    @Delete("DELETE FROM platform_risk_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default PlatformRiskItem save(PlatformRiskItem entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<PlatformRiskItem> entities) {
        for (PlatformRiskItem entity : entities) {
            save(entity);
        }
    }

    default void delete(PlatformRiskItem entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<PlatformRiskItem> entities) {
        for (PlatformRiskItem entity : entities) {
            delete(entity);
        }
    }

    default Page<PlatformRiskItem> findAll(Pageable pageable) {
        List<PlatformRiskItem> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM platform_risk_items ORDER BY created_at desc</script>")
    @ResultMap("PlatformRiskItemMap")
    List<PlatformRiskItem> findAllByOrderByCreatedAtDesc();

    @Select("<script>SELECT * FROM platform_risk_items ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("PlatformRiskItemMap")
    List<PlatformRiskItem> findAllByOrderByCreatedAtDescPage(@Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM platform_risk_items</script>")
    long countFindAllByOrderByCreatedAtDesc();

    default Page<PlatformRiskItem> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        List<PlatformRiskItem> content = findAllByOrderByCreatedAtDescPage(pageable);
        return new PageImpl<>(content, pageable, countFindAllByOrderByCreatedAtDesc());
    }

}
