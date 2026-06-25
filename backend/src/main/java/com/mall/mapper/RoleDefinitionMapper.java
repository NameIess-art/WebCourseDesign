package com.mall.mapper;

import com.mall.entity.RoleDefinition;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class RoleDefinitionMapper extends MyBatisMapperSupport<RoleDefinition> {

    public RoleDefinitionMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, RoleDefinition.class);
    }
}
