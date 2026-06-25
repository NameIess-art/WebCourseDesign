package com.mall.mapper;

import com.mall.entity.ContentAuditItem;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class ContentAuditItemMapper extends MyBatisMapperSupport<ContentAuditItem> {

    public ContentAuditItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, ContentAuditItem.class);
    }
}
