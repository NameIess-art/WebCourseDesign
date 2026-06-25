package com.mall.mapper;

import com.mall.entity.Announcement;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper extends MyBatisMapperSupport<Announcement> {

    public AnnouncementMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, Announcement.class);
    }
}
