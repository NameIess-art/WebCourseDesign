package com.mall.mapper;

import com.mall.entity.OrderModifyRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class OrderModifyRecordMapper extends MyBatisMapperSupport<OrderModifyRecord> {

    public OrderModifyRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, OrderModifyRecord.class);
    }
}
