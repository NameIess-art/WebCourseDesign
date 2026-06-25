package com.mall.mapper;

import com.mall.entity.MemberMessage;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberMessageMapper extends MyBatisMapperSupport<MemberMessage> {

    public MemberMessageMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, MemberMessage.class);
    }

    public List<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc");
    }

    public Page<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc", pageable);
    }

    public long countByUserIdAndReadFlagFalse(Long userId) {
        return countWhere("t.user_id = #{params.userId} and t.read_flag = false", params("userId", userId));
    }
}
