package com.mall.mapper;

import com.mall.entity.OrderEntity;
import com.mall.entity.OrderItem;
import com.mall.enums.OrderStatus;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OrderMapper extends MyBatisMapperSupport<OrderEntity> {

    private final MyBatisMapperSupport<OrderItem> orderItemSupport;

    public OrderMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, OrderEntity.class);
        this.orderItemSupport = new MyBatisMapperSupport<>(sqlMapper, OrderItem.class) { };
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        return Optional.ofNullable(withItems(selectOne("t.id = #{params.id}", params("id", id))));
    }

    @Override
    public OrderEntity save(OrderEntity order) {
        super.save(order);
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            executeUpdate("delete from order_items where order_id = #{params.orderId}", params("orderId", order.getId()));
            order.getItems().forEach(item -> {
                item.setOrder(order);
                orderItemSupport.save(item);
            });
        }
        return withItems(order);
    }

    public List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return withItems(selectList("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc"));
    }

    public Page<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return withItems(selectPage("t.user_id = #{params.userId}", params("userId", userId), "t.created_at desc", pageable));
    }

    public Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return withItems(selectPage(null, params(), "t.created_at desc", pageable));
    }

    public Page<OrderEntity> findByMerchantIdOrderByCreatedAtDesc(Long merchantId, Pageable pageable) {
        return withItems(selectPage("t.merchant_id = #{params.merchantId}", params("merchantId", merchantId), "t.created_at desc", pageable));
    }

    public Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable) {
        return withItems(selectPage("t.status = #{params.status}", params("status", status.name()), "t.created_at desc", pageable));
    }

    public Page<OrderEntity> findByMerchantIdAndStatusOrderByCreatedAtDesc(Long merchantId, OrderStatus status, Pageable pageable) {
        return withItems(selectPage("t.merchant_id = #{params.merchantId} and t.status = #{params.status}", params("merchantId", merchantId, "status", status.name()), "t.created_at desc", pageable));
    }

    public long countByStatus(OrderStatus status) {
        return countWhere("t.status = #{params.status}", params("status", status.name()));
    }

    public long countByMerchantId(Long merchantId) {
        return countWhere("t.merchant_id = #{params.merchantId}", params("merchantId", merchantId));
    }

    public long countByMerchantIdAndStatus(Long merchantId, OrderStatus status) {
        return countWhere("t.merchant_id = #{params.merchantId} and t.status = #{params.status}", params("merchantId", merchantId, "status", status.name()));
    }

    public long countByStatusIn(List<OrderStatus> statuses) {
        Map<String, Object> sqlParams = params();
        return countWhere("t.status in " + statusIn(statuses, sqlParams), sqlParams);
    }

    public BigDecimal sumTotalAmountByStatusIn(List<OrderStatus> statuses) {
        Map<String, Object> sqlParams = params();
        return sumWhere("t.total_amount", "t.status in " + statusIn(statuses, sqlParams), sqlParams);
    }

    public BigDecimal sumTotalAmountByMerchantIdAndStatusIn(Long merchantId, List<OrderStatus> statuses) {
        Map<String, Object> sqlParams = params("merchantId", merchantId);
        return sumWhere("t.total_amount", "t.merchant_id = #{params.merchantId} and t.status in " + statusIn(statuses, sqlParams), sqlParams);
    }

    public long countPaidOrdersBetween(LocalDateTime startAt, LocalDateTime endAt) {
        return countWhere("t.paid_at is not null and t.paid_at >= #{params.startAt} and t.paid_at < #{params.endAt}", params("startAt", startAt, "endAt", endAt));
    }

    public BigDecimal sumPaidOrderAmountBetween(LocalDateTime startAt, LocalDateTime endAt) {
        return sumWhere("t.total_amount", "t.paid_at is not null and t.paid_at >= #{params.startAt} and t.paid_at < #{params.endAt}", params("startAt", startAt, "endAt", endAt));
    }

    private String statusIn(List<OrderStatus> statuses, Map<String, Object> sqlParams) {
        return inClause("status", statuses.stream().map(OrderStatus::name).toList(), sqlParams);
    }

    private OrderEntity withItems(OrderEntity order) {
        if (order != null) {
            order.setItems(orderItems(order.getId()));
        }
        return order;
    }

    private List<OrderEntity> withItems(List<OrderEntity> orders) {
        orders.forEach(this::withItems);
        return orders;
    }

    private Page<OrderEntity> withItems(Page<OrderEntity> page) {
        List<OrderEntity> content = withItems(page.getContent());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private List<OrderItem> orderItems(Long orderId) {
        return orderItemSupport.selectRawList(orderItemSupport.selectSql() + " where t.order_id = #{params.orderId} order by t.id asc", params("orderId", orderId));
    }
}
