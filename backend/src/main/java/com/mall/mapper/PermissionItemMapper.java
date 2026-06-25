package com.mall.mapper;

import com.mall.entity.PermissionItem;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class PermissionItemMapper extends MyBatisMapperSupport<PermissionItem> {

    public PermissionItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, PermissionItem.class);
    }
}
