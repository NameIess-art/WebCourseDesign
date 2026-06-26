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
public interface IdempotentRecordMapper {

    @Select("SELECT * FROM idempotent_records WHERE id = #{id}")
    @Results(id = "IdempotentRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "idemKey", column = "idem_key"),
        @Result(property = "bizType", column = "biz_type"),
        @Result(property = "bizResult", column = "biz_result"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<IdempotentRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM idempotent_records")
    @ResultMap("IdempotentRecordMap")
    List<IdempotentRecord> findAll();

    @Select("SELECT * FROM idempotent_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("IdempotentRecordMap")
    List<IdempotentRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM idempotent_records")
    long count();

    @Insert("INSERT INTO idempotent_records (idem_key, biz_type, biz_result, created_at) VALUES (#{idemKey}, #{bizType}, #{bizResult}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(IdempotentRecord entity);

    @Update("UPDATE idempotent_records SET idem_key = #{idemKey}, biz_type = #{bizType}, biz_result = #{bizResult}, created_at = #{createdAt} WHERE id = #{id}")
    int update(IdempotentRecord entity);

    @Delete("DELETE FROM idempotent_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default IdempotentRecord save(IdempotentRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<IdempotentRecord> entities) {
        for (IdempotentRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(IdempotentRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<IdempotentRecord> entities) {
        for (IdempotentRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<IdempotentRecord> findAll(Pageable pageable) {
        List<IdempotentRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT COUNT(*) FROM idempotent_records WHERE idem_key = #{idemKey}</script>")
    long countForExistsByIdemKey(@Param("idemKey") String idemKey);

    default boolean existsByIdemKey(String idemKey) {
        return countForExistsByIdemKey(idemKey) > 0;
    }

}
