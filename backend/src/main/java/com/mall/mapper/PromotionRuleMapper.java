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
public interface PromotionRuleMapper {

    @Select("SELECT * FROM promotion_rules WHERE id = #{id}")
    @Results(id = "PromotionRuleMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "title", column = "title"),
        @Result(property = "promotionType", column = "promotion_type"),
        @Result(property = "thresholdAmount", column = "threshold_amount"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "ruleText", column = "rule_text"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<PromotionRule> findById(@Param("id") Long id);

    @Select("SELECT * FROM promotion_rules")
    @ResultMap("PromotionRuleMap")
    List<PromotionRule> findAll();

    @Select("SELECT * FROM promotion_rules LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("PromotionRuleMap")
    List<PromotionRule> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM promotion_rules")
    long count();

    @Insert("INSERT INTO promotion_rules (title, promotion_type, threshold_amount, discount_amount, rule_text, status, created_at) VALUES (#{title}, #{promotionType}, #{thresholdAmount}, #{discountAmount}, #{ruleText}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PromotionRule entity);

    @Update("UPDATE promotion_rules SET title = #{title}, promotion_type = #{promotionType}, threshold_amount = #{thresholdAmount}, discount_amount = #{discountAmount}, rule_text = #{ruleText}, status = #{status}, created_at = #{createdAt} WHERE id = #{id}")
    int update(PromotionRule entity);

    @Delete("DELETE FROM promotion_rules WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default PromotionRule save(PromotionRule entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<PromotionRule> entities) {
        for (PromotionRule entity : entities) {
            save(entity);
        }
    }

    default void delete(PromotionRule entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<PromotionRule> entities) {
        for (PromotionRule entity : entities) {
            delete(entity);
        }
    }

    default Page<PromotionRule> findAll(Pageable pageable) {
        List<PromotionRule> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM promotion_rules WHERE status = #{status} ORDER BY created_at desc</script>")
    @ResultMap("PromotionRuleMap")
    List<PromotionRule> findByStatusOrderByCreatedAtDesc(@Param("status") String status);

}
