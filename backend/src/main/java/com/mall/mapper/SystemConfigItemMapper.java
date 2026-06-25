package com.mall.mapper;

import com.mall.entity.SystemConfigItem;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SystemConfigItemMapper extends MyBatisMapperSupport<SystemConfigItem> {

    public SystemConfigItemMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, SystemConfigItem.class);
    }

    public Optional<SystemConfigItem> findByConfigKey(String configKey) {
        return Optional.ofNullable(selectOne("t.config_key = #{params.configKey}", params("configKey", configKey)));
    }
}
