package com.mall.service;

import com.mall.dto.ConfigRequest;
import com.mall.dto.MarketingActivityRequest;
import com.mall.entity.MarketingActivity;
import com.mall.entity.PlatformRiskItem;
import com.mall.entity.SystemConfigItem;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.MarketingActivityRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.PlatformRiskItemRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.SystemConfigItemRepository;
import com.mall.vo.HighConcurrencyResponse;
import com.mall.vo.MarketingActivityResponse;
import com.mall.vo.OperationBoardResponse;
import com.mall.vo.PlatformRiskResponse;
import com.mall.vo.ReportResponse;
import com.mall.vo.SystemConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final MarketingActivityRepository marketingActivityRepository;
    private final SystemConfigItemRepository systemConfigItemRepository;
    private final PlatformRiskItemRepository platformRiskItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OperationBoardResponse board() {
        return new OperationBoardResponse(
                List.of("首页推荐", "分类导航", "搜索联想", "商品详情", "评价问答", "收藏加购", "结算支付", "售后物流", "会员消息"),
                List.of("商品上下架", "SKU/库存/规格", "订单审核发货", "对账售后", "限时活动", "拼团秒杀", "分销预售", "销量转化看板"),
                List.of("全域活动配置", "会场搭建", "规则审核", "排期管理", "RBAC", "内容审核", "商家管理", "系统配置", "用户画像"),
                List.of("JWT 权限", "统一异常", "数据初始化", "H2/MySQL 双环境", "前后端分离 API"),
                List.of("条件扣库存", "乐观锁版本", "用户级限流", "热点秒杀库存", "事务一致性", "缓存/MQ 扩展位")
        );
    }

    @Transactional(readOnly = true)
    public List<MarketingActivityResponse> activities() {
        return marketingActivityRepository.findAllByOrderByStartAtDesc().stream()
                .map(this::toActivityResponse)
                .toList();
    }

    @Transactional
    public MarketingActivityResponse createActivity(MarketingActivityRequest request) {
        MarketingActivity activity = new MarketingActivity();
        activity.setTitle(request.title());
        activity.setType(request.type());
        activity.setRuleText(request.ruleText());
        activity.setStatus("PENDING_REVIEW");
        activity.setStartAt(LocalDateTime.now().plusHours(2));
        activity.setEndAt(LocalDateTime.now().plusDays(7));
        return toActivityResponse(marketingActivityRepository.save(activity));
    }

    @Transactional(readOnly = true)
    public List<SystemConfigResponse> configs() {
        return systemConfigItemRepository.findAll().stream()
                .map(this::toConfigResponse)
                .toList();
    }

    @Transactional
    public SystemConfigResponse upsertConfig(String key, ConfigRequest request) {
        SystemConfigItem item = systemConfigItemRepository.findByConfigKey(key).orElseGet(SystemConfigItem::new);
        item.setConfigKey(key);
        item.setConfigValue(request.value());
        item.setDescription(request.description());
        return toConfigResponse(systemConfigItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<PlatformRiskResponse> risks() {
        return platformRiskItemRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toRiskResponse)
                .toList();
    }

    @Transactional
    public PlatformRiskResponse resolveRisk(Long id) {
        PlatformRiskItem item = platformRiskItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Risk item not found"));
        item.setStatus("RESOLVED");
        return toRiskResponse(item);
    }

    @Transactional(readOnly = true)
    public ReportResponse reports() {
        BigDecimal revenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID
                        || order.getStatus() == OrderStatus.SHIPPED
                        || order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> order.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int stockWarningCount = (int) productRepository.findAll().stream()
                .filter(product -> product.getStock() <= 10)
                .count();
        long orders = orderRepository.count();
        return new ReportResponse(
                Math.max(1200L, orders * 37),
                orders,
                revenue,
                stockWarningCount,
                "核心用户集中在 20-35 岁价格敏感型与品质升级型人群",
                "秒杀与满减活动带来更高转化，适合作为首页推荐和会场主入口"
        );
    }

    public HighConcurrencyResponse highConcurrency() {
        return new HighConcurrencyResponse(
                "数据库条件更新扣减库存: update stock = stock - n where stock >= n，避免超卖",
                "秒杀接口使用用户+活动维度滑动窗口限流",
                "商品详情、分类、活动会场适合接入 Redis 热点缓存与本地缓存",
                "支付回调、物流轨迹、消息推送可通过 RocketMQ 异步解耦",
                "下单、扣库存、清购物车在同一事务内完成，微服务拆分后可接入 Seata Saga/TCC",
                List.of("压测商品列表分页", "压测购物车结算", "压测秒杀扣库存", "校验无负库存", "校验订单幂等")
        );
    }

    private MarketingActivityResponse toActivityResponse(MarketingActivity activity) {
        return new MarketingActivityResponse(activity.getId(), activity.getTitle(), activity.getType(),
                activity.getRuleText(), activity.getStatus(), activity.getStartAt(), activity.getEndAt());
    }

    private SystemConfigResponse toConfigResponse(SystemConfigItem item) {
        return new SystemConfigResponse(item.getId(), item.getConfigKey(), item.getConfigValue(), item.getDescription());
    }

    private PlatformRiskResponse toRiskResponse(PlatformRiskItem item) {
        return new PlatformRiskResponse(item.getId(), item.getTarget(), item.getRiskType(),
                item.getDescription(), item.getStatus(), item.getCreatedAt());
    }
}
