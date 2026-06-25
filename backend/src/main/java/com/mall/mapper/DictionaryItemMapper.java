package com.mall.mapper;

import com.mall.entity.DictionaryItem;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class DictionaryItemMapper extends MyBatisMapperSupport<DictionaryItem> {

    public DictionaryItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, DictionaryItem.class);
    }
}
