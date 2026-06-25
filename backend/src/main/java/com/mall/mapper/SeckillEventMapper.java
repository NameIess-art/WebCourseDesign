package com.mall.mapper;

import com.mall.entity.SeckillEvent;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillEventMapper extends MyBatisMapperSupport<SeckillEvent> {

    public SeckillEventMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, SeckillEvent.class);
    }

    public List<SeckillEvent> findByActiveTrueOrderByStartAtDesc() {
        return selectList("t.active = true", params(), "t.start_at desc");
    }

    public Page<SeckillEvent> findByActiveTrueOrderByStartAtDesc(Pageable pageable) {
        return selectPage("t.active = true", params(), "t.start_at desc", pageable);
    }

    public int decreaseStock(Long eventId) {
        return executeUpdate("update seckill_events set stock = stock - 1, sold = sold + 1 where id = #{params.eventId} and active = true and stock > 0", params("eventId", eventId));
    }
}
