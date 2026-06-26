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
public interface ReconciliationRecordMapper {

    @Select("SELECT * FROM reconciliation_records WHERE id = #{id}")
    @Results(id = "ReconciliationRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "bizDate", column = "biz_date"),
        @Result(property = "orderCount", column = "order_count"),
        @Result(property = "orderAmount", column = "order_amount"),
        @Result(property = "paymentAmount", column = "payment_amount"),
        @Result(property = "status", column = "status")
    })
    Optional<ReconciliationRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM reconciliation_records")
    @ResultMap("ReconciliationRecordMap")
    List<ReconciliationRecord> findAll();

    @Select("SELECT * FROM reconciliation_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("ReconciliationRecordMap")
    List<ReconciliationRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM reconciliation_records")
    long count();

    @Insert("INSERT INTO reconciliation_records (biz_date, order_count, order_amount, payment_amount, status) VALUES (#{bizDate}, #{orderCount}, #{orderAmount}, #{paymentAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReconciliationRecord entity);

    @Update("UPDATE reconciliation_records SET biz_date = #{bizDate}, order_count = #{orderCount}, order_amount = #{orderAmount}, payment_amount = #{paymentAmount}, status = #{status} WHERE id = #{id}")
    int update(ReconciliationRecord entity);

    @Delete("DELETE FROM reconciliation_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default ReconciliationRecord save(ReconciliationRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<ReconciliationRecord> entities) {
        for (ReconciliationRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(ReconciliationRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<ReconciliationRecord> entities) {
        for (ReconciliationRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<ReconciliationRecord> findAll(Pageable pageable) {
        List<ReconciliationRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

    @Select("<script>SELECT * FROM reconciliation_records</script>")
    @ResultMap("ReconciliationRecordMap")
    Optional<ReconciliationRecord> findByBizDate(@Param("bizDate") LocalDate bizDate);

}
