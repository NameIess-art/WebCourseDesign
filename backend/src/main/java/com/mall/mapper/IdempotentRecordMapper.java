package com.mall.mapper;

import com.mall.entity.IdempotentRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class IdempotentRecordMapper extends MyBatisMapperSupport<IdempotentRecord> {

    public IdempotentRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, IdempotentRecord.class);
    }

    public boolean existsByIdemKey(String idemKey) {
        return countWhere("t.idem_key = #{params.idemKey}", params("idemKey", idemKey)) > 0;
    }
}
