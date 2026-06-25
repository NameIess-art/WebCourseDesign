package com.mall.mapper;

import com.mall.entity.PromotionRule;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromotionRuleMapper extends MyBatisMapperSupport<PromotionRule> {

    public PromotionRuleMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, PromotionRule.class);
    }

    public List<PromotionRule> findByStatusOrderByCreatedAtDesc(String status) {
        return selectList("t.status = #{params.status}", params("status", status), "t.created_at desc");
    }
}
