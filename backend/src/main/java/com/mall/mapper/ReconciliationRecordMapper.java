package com.mall.mapper;

import com.mall.entity.ReconciliationRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ReconciliationRecordMapper extends MyBatisMapperSupport<ReconciliationRecord> {

    public ReconciliationRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ReconciliationRecord.class);
    }

    public Optional<ReconciliationRecord> findByBizDate(LocalDate bizDate) {
        return Optional.ofNullable(selectOne("t.biz_date = #{params.bizDate}", params("bizDate", bizDate)));
    }
}
