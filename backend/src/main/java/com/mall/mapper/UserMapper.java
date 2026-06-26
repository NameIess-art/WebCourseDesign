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
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = "UserAccountMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "email", column = "email"),
        @Result(property = "displayName", column = "display_name"),
        @Result(property = "role", column = "role"),
        @Result(property = "points", column = "points"),
        @Result(property = "memberLevel", column = "member_level"),
        @Result(property = "merchant", column = "merchant_id", one = @One(select = "com.mall.mapper.MerchantMapper.findById"))
    })
    Optional<UserAccount> findById(@Param("id") Long id);

    @Select("SELECT * FROM users")
    @ResultMap("UserAccountMap")
    List<UserAccount> findAll();

    @Select("SELECT * FROM users LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("UserAccountMap")
    List<UserAccount> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM users")
    long count();

    @Insert("INSERT INTO users (username, password, email, display_name, role, points, member_level, merchant_id) VALUES (#{username}, #{password}, #{email}, #{displayName}, #{role}, #{points}, #{memberLevel}, #{merchant.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAccount entity);

    @Update("UPDATE users SET username = #{username}, password = #{password}, email = #{email}, display_name = #{displayName}, role = #{role}, points = #{points}, member_level = #{memberLevel}, merchant_id = #{merchant.id} WHERE id = #{id}")
    int update(UserAccount entity);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default UserAccount save(UserAccount entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<UserAccount> entities) {
        for (UserAccount entity : entities) {
            save(entity);
        }
    }

    default void delete(UserAccount entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<UserAccount> entities) {
        for (UserAccount entity : entities) {
            delete(entity);
        }
    }

    default Page<UserAccount> findAll(Pageable pageable) {
        List<UserAccount> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM users WHERE username = #{username}</script>")
    @ResultMap("UserAccountMap")
    Optional<UserAccount> findByUsername(@Param("username") String username);

    @Select("<script>SELECT * FROM users</script>")
    @ResultMap("UserAccountMap")
    Optional<UserAccount> findByEmail(@Param("email") String email);

    @Select("<script>SELECT COUNT(*) FROM users WHERE username = #{username}</script>")
    long countForExistsByUsername(@Param("username") String username);

    default boolean existsByUsername(String username) {
        return countForExistsByUsername(username) > 0;
    }

    @Select("<script>SELECT COUNT(*) FROM users WHERE email = #{email}</script>")
    long countForExistsByEmail(@Param("email") String email);

    default boolean existsByEmail(String email) {
        return countForExistsByEmail(email) > 0;
    }

    @Select("<script>SELECT COUNT(*) FROM users</script>")
    long countByMerchantId(@Param("merchantId") Long merchantId);

}
