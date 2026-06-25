package com.mall.mapper;

import com.mall.entity.MerchantPenalty;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class MerchantPenaltyMapper extends MyBatisMapperSupport<MerchantPenalty> {

    public MerchantPenaltyMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, MerchantPenalty.class);
    }
}
