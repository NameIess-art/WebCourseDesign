package com.mall.mapper;

import com.mall.entity.MarketingActivity;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketingActivityMapper extends MyBatisMapperSupport<MarketingActivity> {

    public MarketingActivityMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, MarketingActivity.class);
    }

    public List<MarketingActivity> findAllByOrderByStartAtDesc() {
        return selectList(null, params(), "t.start_at desc");
    }

    public Page<MarketingActivity> findAllByOrderByStartAtDesc(Pageable pageable) {
        return selectPage(null, params(), "t.start_at desc", pageable);
    }

    public List<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(String status) {
        return selectList("lower(t.status) = lower(#{params.status})", params("status", status), "t.start_at desc");
    }

    public Page<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(String status, Pageable pageable) {
        return selectPage("lower(t.status) = lower(#{params.status})", params("status", status), "t.start_at desc", pageable);
    }
}
