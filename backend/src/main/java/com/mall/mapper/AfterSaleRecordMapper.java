package com.mall.mapper;

import com.mall.entity.AfterSaleRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AfterSaleRecordMapper extends MyBatisMapperSupport<AfterSaleRecord> {

    public AfterSaleRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, AfterSaleRecord.class);
    }

    public List<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc");
    }

    public Page<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc", pageable);
    }
}
