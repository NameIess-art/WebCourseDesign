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
public interface OrderModifyRecordMapper {

    @Select("SELECT * FROM order_modify_records WHERE id = #{id}")
    @Results(id = "OrderModifyRecordMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "order", column = "order_id", one = @One(select = "com.mall.mapper.OrderMapper.findById")),
        @Result(property = "beforeAmount", column = "before_amount"),
        @Result(property = "afterAmount", column = "after_amount"),
        @Result(property = "beforeAddress", column = "before_address"),
        @Result(property = "afterAddress", column = "after_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<OrderModifyRecord> findById(@Param("id") Long id);

    @Select("SELECT * FROM order_modify_records")
    @ResultMap("OrderModifyRecordMap")
    List<OrderModifyRecord> findAll();

    @Select("SELECT * FROM order_modify_records LIMIT #{pageable.pageSize} OFFSET #{pageable.offset}")
    @ResultMap("OrderModifyRecordMap")
    List<OrderModifyRecord> findAllPage(@Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM order_modify_records")
    long count();

    @Insert("INSERT INTO order_modify_records (order_id, before_amount, after_amount, before_address, after_address, remark, created_at) VALUES (#{order.id}, #{beforeAmount}, #{afterAmount}, #{beforeAddress}, #{afterAddress}, #{remark}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderModifyRecord entity);

    @Update("UPDATE order_modify_records SET order_id = #{order.id}, before_amount = #{beforeAmount}, after_amount = #{afterAmount}, before_address = #{beforeAddress}, after_address = #{afterAddress}, remark = #{remark}, created_at = #{createdAt} WHERE id = #{id}")
    int update(OrderModifyRecord entity);

    @Delete("DELETE FROM order_modify_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    default OrderModifyRecord save(OrderModifyRecord entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
    
    default void saveAll(Collection<OrderModifyRecord> entities) {
        for (OrderModifyRecord entity : entities) {
            save(entity);
        }
    }

    default void delete(OrderModifyRecord entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    default void deleteAll(Collection<OrderModifyRecord> entities) {
        for (OrderModifyRecord entity : entities) {
            delete(entity);
        }
    }

    default Page<OrderModifyRecord> findAll(Pageable pageable) {
        List<OrderModifyRecord> content = findAllPage(pageable);
        return new PageImpl<>(content, pageable, count());
    }

}
