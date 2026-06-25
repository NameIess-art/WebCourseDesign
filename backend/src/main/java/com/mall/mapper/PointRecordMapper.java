package com.mall.mapper;

import com.mall.entity.PointRecord;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointRecordMapper extends MyBatisMapperSupport<PointRecord> {

    public PointRecordMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, PointRecord.class);
    }

    public List<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc");
    }

    public Page<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc", pageable);
    }
}
