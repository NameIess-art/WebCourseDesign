package com.mall.mapper;

import com.mall.entity.OrderAuditRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class OrderAuditRecordMapper extends MyBatisMapperSupport<OrderAuditRecord> {

    public OrderAuditRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, OrderAuditRecord.class);
    }
}
