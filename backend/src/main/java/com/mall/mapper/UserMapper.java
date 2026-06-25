package com.mall.mapper;

import com.mall.entity.UserAccount;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper extends MyBatisMapperSupport<UserAccount> {

    public UserMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, UserAccount.class);
    }

    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(selectOne("t.username = #{params.username}", params("username", username)));
    }

    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(selectOne("t.email = #{params.email}", params("email", email)));
    }

    public boolean existsByUsername(String username) {
        return countWhere("t.username = #{params.username}", params("username", username)) > 0;
    }

    public boolean existsByEmail(String email) {
        return countWhere("t.email = #{params.email}", params("email", email)) > 0;
    }

    public long countByMerchantId(Long merchantId) {
        return countWhere("t.merchant_id = #{params.merchantId}", params("merchantId", merchantId));
    }
}
