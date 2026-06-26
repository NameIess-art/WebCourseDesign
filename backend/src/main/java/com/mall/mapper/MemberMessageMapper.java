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
public interface MemberMessageMapper {

    @Select("SELECT * FROM member_messages WHERE id = #{id}")
    @Results(id = "MemberMessageMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "user", column = "user_id", one = @One(select = "com.mall.mapper.UserMapper.findById")),
        @Result(property = "title", column = "title"),
        @Result(property = "content", column = "content"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<MemberMessage> findById(@Param("id") Long id);

    @Select("SELECT * FROM member_messages")
    @ResultMap("MemberMessageMap")
    List<MemberMessage> findAll();

    @Select("SELECT * FROM member_messages LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("MemberMessageMap")
    List<MemberMessage> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM member_messages")
    long count();

    @Insert("INSERT INTO member_messages (user_id, title, content, read_flag, created_at) VALUES (#{user.id}, #{title}, #{content}, #{readFlag}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MemberMessage entity);

    @Update("UPDATE member_messages SET user_id = #{user.id}, title = #{title}, content = #{content}, read_flag = #{readFlag}, created_at = #{createdAt} WHERE id = #{id}")
    int update(MemberMessage entity);

    @Delete("DELETE FROM member_messages WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default MemberMessage save(MemberMessage entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<MemberMessage> entities) {
        for (MemberMessage entity : entities) {
            save(entity);
        }
    }

    default void delete(MemberMessage entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<MemberMessage> entities) {
        for (MemberMessage entity : entities) {
            delete(entity);
        }
    }

    default Page<MemberMessage> findAll(Pageable pageable) {
        List<MemberMessage> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM member_messages WHERE user_id = #{userId} ORDER BY created_at desc</script>")
    @ResultMap("MemberMessageMap")
    List<MemberMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM member_messages WHERE user_id = #{userId} ORDER BY created_at desc LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}</script>")
    @ResultMap("MemberMessageMap")
    List<MemberMessage> findByUserIdOrderByCreatedAtDescPage(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("<script>SELECT COUNT(*) FROM member_messages WHERE user_id = #{userId}</script>")
    long countFindByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    default Page<MemberMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        List<MemberMessage> content = findByUserIdOrderByCreatedAtDescPage(userId, pageable);
        return new PageImpl<>(content, pageable, countFindByUserIdOrderByCreatedAtDesc(userId));
    }

    @Select("<script>SELECT COUNT(*) FROM member_messages WHERE user_id = #{userId} AND read_flag = false</script>")
    long countByUserIdAndReadFlagFalse(@Param("userId") Long userId);

}
