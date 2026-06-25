package com.mall.mapper;

import com.mall.entity.AddressBook;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressBookMapper extends MyBatisMapperSupport<AddressBook> {

    public AddressBookMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, AddressBook.class);
    }

    public List<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId) {
        return selectList("t.user_id = #{params.userId}", params("userId", userId), "t.default_address desc, t.id desc");
    }

    public Page<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId, Pageable pageable) {
        return selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.default_address desc, t.id desc", pageable);
    }
}
