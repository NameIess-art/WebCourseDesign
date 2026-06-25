package com.mall.mapper;

import com.mall.entity.MarketingFlowRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class MarketingFlowRecordMapper extends MyBatisMapperSupport<MarketingFlowRecord> {

    public MarketingFlowRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, MarketingFlowRecord.class);
    }
}
