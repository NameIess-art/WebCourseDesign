package com.mall.mapper;

import com.mall.entity.Merchant;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class MerchantMapper extends MyBatisMapperSupport<Merchant> {

    public MerchantMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, Merchant.class);
    }
}
