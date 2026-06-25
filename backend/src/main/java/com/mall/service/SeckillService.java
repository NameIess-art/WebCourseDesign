package com.mall.service;

import com.mall.dto.SeckillEventRequest;
import com.mall.entity.OrderEntity;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.entity.ProductSku;
import com.mall.entity.SeckillEvent;
import com.mall.entity.UserAccount;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.ProductSkuMapper;
import com.mall.mapper.SeckillEventMapper;
import com.mall.vo.OrderItemResponse;
import com.mall.vo.PageResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.SeckillEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SeckillService {

    private static final long WINDOW_MILLIS = 10_000L;
    private static final int MAX_REQUESTS = 3;

    private final SeckillEventMapper seckillEventMapper;
    private final ProductMapper productMapper;
    private final ProductSkuMapper productSkuMapper;
    private final OrderMapper orderMapper;
    private final Map<String, Deque<Long>> requestWindows = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public PageResponse<SeckillEventResponse> events(int page, int size) {
        org.springframework.data.domain.Page<SeckillEventResponse> result = seckillEventMapper.findByActiveTrueOrderByStartAtDesc(org.springframework.data.domain.PageRequest.of(page, size))
                .map(this::toResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public SeckillEventResponse createEvent(SeckillEventRequest request) {
        Product product = productMapper.findById(request.productId())
                .orElseThrow(() -> new BusinessException("Product not found"));
        SeckillEvent event = new SeckillEvent();
        event.setProduct(product);
        event.setSeckillPrice(request.seckillPrice());
        event.setStock(request.stock());
        event.setSold(0);
        event.setActive(true);
        event.setStartAt(LocalDateTime.now().minusMinutes(5));
        event.setEndAt(LocalDateTime.now().plusDays(1));
        return toResponse(seckillEventMapper.save(event));
    }

    @Transactional
    public OrderResponse purchase(UserAccount user, Long eventId) {
        rateLimit(user.getId(), eventId);
        SeckillEvent event = seckillEventMapper.findById(eventId)
                .orElseThrow(() -> new BusinessException("Seckill event not found"));
        LocalDateTime now = LocalDateTime.now();
        if (!Boolean.TRUE.equals(event.getActive()) || now.isBefore(event.getStartAt()) || now.isAfter(event.getEndAt())) {
            throw new BusinessException("Seckill event is not active");
        }
        if (seckillEventMapper.decreaseStock(eventId) == 0) {
            throw new BusinessException("Seckill stock sold out");
        }
        if (productMapper.decreaseStock(event.getProduct().getId(), 1) == 0) {
            throw new BusinessException("Product stock sold out");
        }

        Product product = event.getProduct();
        ProductSku sku = productSkuMapper.findByProductIdOrderByIdAsc(product.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("SKU not found"));
        if (productSkuMapper.decreaseStock(sku.getId(), 1) == 0) {
            throw new BusinessException("SKU stock sold out");
        }
        OrderEntity order = new OrderEntity();
        order.setOrderNo("SK" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        order.setUser(user);
        order.setShippingAddress("Seckill order, please confirm delivery address in address center");
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setOriginalAmount(event.getSeckillPrice());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPointsUsed(0);
        order.setPointsDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(event.getSeckillPrice());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setSku(sku);
        item.setQuantity(1);
        item.setPrice(event.getSeckillPrice());
        order.getItems().add(item);

        return toOrderResponse(orderMapper.save(order));
    }

    private void rateLimit(Long userId, Long eventId) {
        String key = userId + ":" + eventId;
        long now = System.currentTimeMillis();
        Deque<Long> window = requestWindows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty() && now - window.peekFirst() > WINDOW_MILLIS) {
                window.removeFirst();
            }
            if (window.size() >= MAX_REQUESTS) {
                throw new BusinessException("Requests are too frequent, please retry later");
            }
            window.addLast(now);
        }
    }

    private SeckillEventResponse toResponse(SeckillEvent event) {
        Product product = event.getProduct();
        return new SeckillEventResponse(event.getId(), product.getId(), product.getName(), product.getImageUrl(),
                event.getSeckillPrice(), event.getStock(), event.getSold(), event.getActive(),
                event.getStartAt(), event.getEndAt());
    }

    private OrderResponse toOrderResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getStatus().name(),
                order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount(),
                order.getOriginalAmount() == null ? BigDecimal.ZERO : order.getOriginalAmount(),
                order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount(),
                order.getPointsUsed() == null ? 0 : order.getPointsUsed(),
                order.getPointsDiscountAmount() == null ? BigDecimal.ZERO : order.getPointsDiscountAmount(),
                order.getPaymentChannel(),
                order.getAuditStatus() == null ? "PENDING" : order.getAuditStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getSku() == null ? null : item.getSku().getId(),
                                item.getSku() == null ? item.getProduct().getSpec() : item.getSku().getSpecName(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList()
        );
    }
}
