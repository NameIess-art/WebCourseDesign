package com.mall.mapper;

import com.mall.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.time.*;
import java.math.*;
import com.mall.enums.*;

@Mapper
public interface AddressBookMapper {

    @Select("SELECT * FROM address_books WHERE id = #{id}")
    @Results(id = "AddressBookMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "receiver", column = "receiver"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "region", column = "region"),
        @Result(property = "detail", column = "detail"),
        @Result(property = "defaultAddress", column = "default_address")
    })
    Optional<AddressBook> findById(@Param("id") Long id);

    @Select("SELECT * FROM address_books")
    @ResultMap("AddressBookMap")
    List<AddressBook> findAll();

    @Select("SELECT * FROM address_books LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("AddressBookMap")
    List<AddressBook> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM address_books")
    long count();

    @Insert("INSERT INTO address_books (user_id, receiver, phone, region, detail, default_address) VALUES (#{user.id}, #{receiver}, #{phone}, #{region}, #{detail}, #{defaultAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AddressBook entity);

    @Update("UPDATE address_books SET user_id = #{user.id}, receiver = #{receiver}, phone = #{phone}, region = #{region}, detail = #{detail}, default_address = #{defaultAddress} WHERE id = #{id}")
    int update(AddressBook entity);

    @Delete("DELETE FROM address_books WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default AddressBook save(AddressBook entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<AddressBook> entities) {
        for (AddressBook entity : entities) {
            save(entity);
        }
    }

    default void delete(AddressBook entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<AddressBook> entities) {
        for (AddressBook entity : entities) {
            delete(entity);
        }
    }

    default Page<AddressBook> findAll(Pageable pageable) {
        List<AddressBook> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM address_books WHERE user_id = #{userId} ORDER BY default_address desc, id desc</script>")
    @ResultMap("AddressBookMap")
    List<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM address_books WHERE user_id = #{userId} ORDER BY default_address desc, id desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("AddressBookMap")
    List<AddressBook> findByUserIdOrderByDefaultAddressDescIdDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM address_books WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByDefaultAddressDescIdDesc(@Param("userId") Long userId);

    default Page<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId, Pageable pageable) {
        List<AddressBook> content = findByUserIdOrderByDefaultAddressDescIdDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByDefaultAddressDescIdDesc(userId));
    }

}
