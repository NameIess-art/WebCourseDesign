package com.mall.mapper;

import com.mall.entity.PlatformRiskItem;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlatformRiskItemMapper extends MyBatisMapperSupport<PlatformRiskItem> {

    public PlatformRiskItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, PlatformRiskItem.class);
    }

    public List<PlatformRiskItem> findAllByOrderByCreatedAtDesc() {
        return selectList(null, params(), "t.created_at desc");
    }

    public Page<PlatformRiskItem> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return selectPage(null, params(), "t.created_at desc", pageable);
    }
}
