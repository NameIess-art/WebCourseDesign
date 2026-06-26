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
public interface OrderAuditRecordMapper {

    @Select("SELECT * FROM order_audit_records WHERE id = #{id}")
    @Results(id = "OrderAuditRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "order", column = "order_id", one = @One(select = "com.mall.mapper.OrderMapper.findById")),
        @Result(property = "approved", column = "approved"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<OrderAuditRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM order_audit_records")
    @ResultMap("OrderAuditRecordMap")
    List<OrderAuditRecord> findAll();

    @Select("SELECT * FROM order_audit_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("OrderAuditRecordMap")
    List<OrderAuditRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM order_audit_records")
    long count();

    @Insert("INSERT INTO order_audit_records (order_id, approved, remark, created_at) VALUES (#{order.id}, #{approved}, #{remark}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderAuditRecord entity);

    @Update("UPDATE order_audit_records SET order_id = #{order.id}, approved = #{approved}, remark = #{remark}, created_at = #{createdAt} WHERE id = #{id}")
    int update(OrderAuditRecord entity);

    @Delete("DELETE FROM order_audit_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default OrderAuditRecord save(OrderAuditRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<OrderAuditRecord> entities) {
        for (OrderAuditRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(OrderAuditRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<OrderAuditRecord> entities) {
        for (OrderAuditRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<OrderAuditRecord> findAll(Pageable pageable) {
        List<OrderAuditRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
