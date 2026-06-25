package com.mall.service;

import com.mall.dto.PaymentCallbackRequest;
import com.mall.dto.PaymentRequest;
import com.mall.entity.IdempotentRecord;
import com.mall.entity.MemberMessage;
import com.mall.entity.OrderEntity;
import com.mall.entity.PaymentRecord;
import com.mall.entity.PointRecord;
import com.mall.entity.UserAccount;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.mapper.IdempotentRecordMapper;
import com.mall.mapper.MemberMessageMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.PaymentRecordMapper;
import com.mall.mapper.PointRecordMapper;
import com.mall.mapper.UserMapper;
import com.mall.vo.PageResponse;
import com.mall.vo.PaymentResponse;
import com.mall.vo.PointRecordResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderMapper orderMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final PointRecordMapper pointRecordMapper;
    private final MemberMessageMapper memberMessageMapper;
    private final IdempotentRecordMapper idempotentRecordMapper;
    private final UserMapper userMapper;

    @Transactional
    public PaymentResponse createPayment(UserAccount user, PaymentRequest request) {
        OrderEntity order = orderMapper.findById(request.orderId())
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("Order cannot be paid in its current state");
        }
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                && idempotentRecordMapper.existsByIdemKey(request.idempotencyKey())) {
            return paymentRecordMapper.findByOrderIdOrderByCreatedAtDesc(order.getId()).stream()
                    .findFirst()
                    .map(this::toResponse)
                    .orElseThrow(() -> new BusinessException("Duplicate payment request"));
        }

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        record.setOrder(order);
        record.setChannel(normalizeChannel(request.channel()));
        record.setAmount(order.getTotalAmount());
        record.setStatus("CREATED");
        record.setCreatedAt(LocalDateTime.now());
        PaymentRecord saved = paymentRecordMapper.save(record);

        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            IdempotentRecord idem = new IdempotentRecord();
            idem.setIdemKey(request.idempotencyKey());
            idem.setBizType("PAYMENT_CREATE");
            idem.setBizResult(saved.getPaymentNo());
            idem.setCreatedAt(LocalDateTime.now());
            idempotentRecordMapper.save(idem);
        }
        return toResponse(saved);
    }

    @Transactional
    public PaymentResponse mockCallback(PaymentCallbackRequest request) {
        PaymentRecord record = paymentRecordMapper.findByPaymentNo(request.paymentNo())
                .orElseThrow(() -> new BusinessException("Payment not found"));
        if ("PAID".equals(record.getStatus())) {
            return toResponse(record);
        }

        LocalDateTime now = LocalDateTime.now();
        record.setStatus("PAID");
        record.setPaidAt(now);
        OrderEntity order = orderMapper.findById(record.getOrder().getId())
                .orElseThrow(() -> new BusinessException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(now);
        order.setPaymentChannel(record.getChannel());

        UserAccount user = userMapper.findById(order.getUser().getId())
                .orElseThrow(() -> new BusinessException("User not found"));
        int earned = order.getTotalAmount().intValue();
        user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + earned);
        userMapper.save(user);
        pointRecordMapper.save(pointRecord(user, earned, "EARN", "Payment success reward"));
        memberMessageMapper.save(message(user, "Payment completed",
                "Order " + order.getOrderNo() + " paid by " + record.getChannel()
                        + ", trade no " + request.channelTradeNo()));
        orderMapper.save(order);
        return toResponse(paymentRecordMapper.save(record));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> payments(UserAccount user, Long orderId) {
        return paymentRecordMapper.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .filter(record -> record.getOrder().getUser().getId().equals(user.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PointRecordResponse> pointRecords(UserAccount user, int page, int size) {
        Page<PointRecordResponse> result = pointRecordMapper.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(record -> new PointRecordResponse(record.getId(), record.getPoints(),
                        record.getType(), record.getDescription(), record.getCreatedAt()));
        return PageResponse.of(result);
    }

    private String normalizeChannel(String channel) {
        String value = channel == null ? "" : channel.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "ALIPAY", "WECHAT", "BANK" -> value;
            default -> throw new BusinessException("Unsupported payment channel");
        };
    }

    private PaymentResponse toResponse(PaymentRecord record) {
        return new PaymentResponse(record.getId(), record.getPaymentNo(), record.getOrder().getId(),
                record.getChannel(), record.getAmount(), record.getStatus(), record.getCreatedAt(), record.getPaidAt());
    }

    private PointRecord pointRecord(UserAccount user, Integer points, String type, String description) {
        PointRecord record = new PointRecord();
        record.setUser(user);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    private MemberMessage message(UserAccount user, String title, String content) {
        MemberMessage message = new MemberMessage();
        message.setUser(user);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(false);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}
