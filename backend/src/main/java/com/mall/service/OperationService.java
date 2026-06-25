package com.mall.service;

import com.mall.dto.AdminTextRequest;
import com.mall.dto.ConfigRequest;
import com.mall.dto.MarketingActivityRequest;
import com.mall.dto.PromotionRuleRequest;
import com.mall.entity.Announcement;
import com.mall.entity.ContentAuditItem;
import com.mall.entity.DictionaryItem;
import com.mall.entity.MarketingActivity;
import com.mall.entity.MarketingFlowRecord;
import com.mall.entity.Merchant;
import com.mall.entity.MerchantPenalty;
import com.mall.entity.PermissionItem;
import com.mall.entity.PlatformRiskItem;
import com.mall.entity.PromotionRule;
import com.mall.entity.RoleDefinition;
import com.mall.entity.SystemConfigItem;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.mapper.AnnouncementMapper;
import com.mall.mapper.ContentAuditItemMapper;
import com.mall.mapper.DictionaryItemMapper;
import com.mall.mapper.MarketingActivityMapper;
import com.mall.mapper.MarketingFlowRecordMapper;
import com.mall.mapper.MerchantPenaltyMapper;
import com.mall.mapper.MerchantMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.PermissionItemMapper;
import com.mall.mapper.PlatformRiskItemMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.PromotionRuleMapper;
import com.mall.mapper.RoleDefinitionMapper;
import com.mall.mapper.SystemConfigItemMapper;
import com.mall.vo.HighConcurrencyResponse;
import com.mall.vo.MarketingActivityResponse;
import com.mall.vo.OperationBoardResponse;
import com.mall.vo.PlatformRiskResponse;
import com.mall.vo.ReportResponse;
import com.mall.vo.SimpleItemResponse;
import com.mall.vo.SystemConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.mall.vo.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final MarketingActivityMapper marketingActivityMapper;
    private final SystemConfigItemMapper systemConfigItemMapper;
    private final PlatformRiskItemMapper platformRiskItemMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final DictionaryItemMapper dictionaryItemMapper;
    private final AnnouncementMapper announcementMapper;
    private final MerchantMapper merchantMapper;
    private final MerchantPenaltyMapper merchantPenaltyMapper;
    private final RoleDefinitionMapper roleDefinitionMapper;
    private final PermissionItemMapper permissionItemMapper;
    private final ContentAuditItemMapper contentAuditItemMapper;
    private final PromotionRuleMapper promotionRuleMapper;
    private final MarketingFlowRecordMapper marketingFlowRecordMapper;

    public OperationBoardResponse board() {
        return new OperationBoardResponse(
                List.of("home recommendations", "category navigation", "search suggest", "product detail",
                        "review and Q&A", "favorite and cart", "checkout and payment", "after-sale and logistics",
                        "member points and messages"),
                List.of("product publish", "SKU/spec/stock", "order audit and shipment", "reconciliation",
                        "after-sale processing", "flash sale", "group-buy", "presale", "bundle", "distribution",
                        "merchant dashboard"),
                List.of("activity operation", "venue configuration", "rule audit", "schedule management",
                        "RBAC", "content audit", "merchant management", "config center", "analytics"),
                List.of("JWT auth", "role control", "global exception", "seed data", "H2/MySQL profiles",
                        "front/back API separation"),
                List.of("conditional stock deduction", "optimistic version idea", "user rate limit",
                        "hot cache design", "idempotency", "MQ and Seata scaffold")
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MarketingActivityResponse> activities(int page, int size) {
        Page<MarketingActivityResponse> result = marketingActivityMapper.findAllByOrderByStartAtDesc(PageRequest.of(page, size)).map(this::toActivityResponse);
        return PageResponse.of(result);
    }

    @Transactional(readOnly = true)
    public PageResponse<MarketingActivityResponse> publicActivities(int page, int size) {
        Page<MarketingActivityResponse> result = marketingActivityMapper.findByStatusIgnoreCaseOrderByStartAtDesc("APPROVED", PageRequest.of(page, size)).map(this::toActivityResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public MarketingActivityResponse createActivity(MarketingActivityRequest request) {
        return createActivity(request, false);
    }

    @Transactional
    public MarketingActivityResponse createActivity(MarketingActivityRequest request, boolean autoApprove) {
        MarketingActivity activity = new MarketingActivity();
        activity.setTitle(request.title());
        activity.setType(request.type());
        activity.setRuleText(request.ruleText());
        activity.setStatus(autoApprove ? "APPROVED" : "PENDING_REVIEW");
        activity.setStartAt(autoApprove ? LocalDateTime.now() : LocalDateTime.now().plusHours(2));
        activity.setEndAt(LocalDateTime.now().plusDays(7));
        return toActivityResponse(marketingActivityMapper.save(activity));
    }

    @Transactional
    public MarketingActivityResponse auditActivity(Long id, boolean approved) {
        MarketingActivity activity = marketingActivityMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Activity not found"));
        activity.setStatus(approved ? "APPROVED" : "REJECTED");
        return toActivityResponse(marketingActivityMapper.save(activity));
    }

    @Transactional(readOnly = true)
    public PageResponse<SystemConfigResponse> configs(int page, int size) {
        Page<SystemConfigResponse> result = systemConfigItemMapper.findAll(PageRequest.of(page, size)).map(this::toConfigResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public SystemConfigResponse upsertConfig(String key, ConfigRequest request) {
        SystemConfigItem item = systemConfigItemMapper.findByConfigKey(key).orElseGet(SystemConfigItem::new);
        item.setConfigKey(key);
        item.setConfigValue(request.value());
        item.setDescription(request.description());
        return toConfigResponse(systemConfigItemMapper.save(item));
    }

    @Transactional
    public void deleteConfig(String key) {
        systemConfigItemMapper.findByConfigKey(key).ifPresent(systemConfigItemMapper::delete);
    }

    @Transactional(readOnly = true)
    public PageResponse<PlatformRiskResponse> risks(int page, int size) {
        Page<PlatformRiskResponse> result = platformRiskItemMapper.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)).map(this::toRiskResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public PlatformRiskResponse resolveRisk(Long id) {
        PlatformRiskItem item = platformRiskItemMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Risk item not found"));
        item.setStatus("RESOLVED");
        return toRiskResponse(item);
    }

    @Transactional(readOnly = true)
    public ReportResponse reports() {
        List<OrderStatus> revenueStatuses = List.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED);
        BigDecimal revenue = orderMapper.sumTotalAmountByStatusIn(revenueStatuses);
        int stockWarningCount = (int) productMapper.countByStockLessThanEqual(10);
        long orders = orderMapper.count();
        return new ReportResponse(
                Math.max(1200L, orders * 37),
                orders,
                revenue,
                stockWarningCount,
                "Core users are price-sensitive repeat buyers and quality-upgrade buyers.",
                "Flash sale and full-reduction campaigns have higher conversion and are suitable for home traffic."
        );
    }

    public HighConcurrencyResponse highConcurrency() {
        return new HighConcurrencyResponse(
                "Use update stock = stock - n where stock >= n for normal orders; seckill can pre-deduct in Redis.",
                "Use user + activity sliding window rate limit locally and map to Sentinel rules in microservice mode.",
                "Cache product detail, categories, recommendations and campaign venues in Redis with active invalidation.",
                "Payment callback, logistics trace, message push and seckill ordering are modeled as RocketMQ events.",
                "Single app uses local transaction; microservice scaffold uses Seata for order/product/member consistency.",
                List.of("product list pressure test", "checkout pressure test", "seckill stock deduction test",
                        "no negative stock validation", "idempotency validation", "payment callback replay test")
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> dictionaries(int page, int size) {
        Page<SimpleItemResponse> result = dictionaryItemMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getDictKey(), item.getDictType(), item.getDictValue(), "ACTIVE"));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createDictionary(AdminTextRequest request) {
        DictionaryItem item = new DictionaryItem();
        item.setDictType(request.type());
        item.setDictKey(request.title());
        item.setDictValue(request.content());
        DictionaryItem saved = dictionaryItemMapper.save(item);
        return new SimpleItemResponse(saved.getId(), saved.getDictKey(), saved.getDictType(),
                saved.getDictValue(), "ACTIVE");
    }

    @Transactional
    public void deleteDictionary(Long id) {
        dictionaryItemMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> announcements(int page, int size) {
        Page<SimpleItemResponse> result = announcementMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getTitle(), item.getPopup() ? "POPUP" : "NOTICE", item.getContent(), "ACTIVE"));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createAnnouncement(AdminTextRequest request) {
        Announcement item = new Announcement();
        item.setTitle(request.title());
        item.setContent(request.content());
        item.setPopup("POPUP".equalsIgnoreCase(request.type()));
        item.setCreatedAt(LocalDateTime.now());
        Announcement saved = announcementMapper.save(item);
        return new SimpleItemResponse(saved.getId(), saved.getTitle(), saved.getPopup() ? "POPUP" : "NOTICE",
                saved.getContent(), "ACTIVE");
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> merchants(int page, int size) {
        Page<SimpleItemResponse> result = merchantMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getName(), "MERCHANT", item.getContactPhone(), item.getStatus()));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createMerchant(AdminTextRequest request) {
        Merchant merchant = new Merchant();
        merchant.setName(request.title());
        merchant.setContactPhone(request.content());
        merchant.setStatus(request.type() == null || request.type().isBlank() ? "ACTIVE" : request.type());
        merchant.setCreatedAt(LocalDateTime.now());
        Merchant saved = merchantMapper.save(merchant);
        return new SimpleItemResponse(saved.getId(), saved.getName(), "MERCHANT", saved.getContactPhone(), saved.getStatus());
    }

    @Transactional
    public SimpleItemResponse updateMerchantStatus(Long id, String status) {
        Merchant merchant = merchantMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Merchant not found"));
        merchant.setStatus(status);
        Merchant saved = merchantMapper.save(merchant);
        return new SimpleItemResponse(saved.getId(), saved.getName(), "MERCHANT", saved.getContactPhone(), saved.getStatus());
    }

    @Transactional
    public SimpleItemResponse penalizeMerchant(Long id, AdminTextRequest request) {
        Merchant merchant = merchantMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Merchant not found"));
        MerchantPenalty penalty = new MerchantPenalty();
        penalty.setMerchant(merchant);
        penalty.setPenaltyType(request.type());
        penalty.setReason(request.content());
        penalty.setCreatedAt(LocalDateTime.now());
        MerchantPenalty saved = merchantPenaltyMapper.save(penalty);
        merchant.setStatus("PENALIZED");
        return new SimpleItemResponse(saved.getId(), merchant.getName(), saved.getPenaltyType(),
                saved.getReason(), merchant.getStatus());
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> roles(int page, int size) {
        Page<SimpleItemResponse> result = roleDefinitionMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getName(), item.getCode(), "menus=" + item.getMenuPermissions() + "; buttons=" + item.getButtonPermissions(), "ACTIVE"));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createRole(AdminTextRequest request) {
        RoleDefinition role = new RoleDefinition();
        role.setCode(request.title());
        role.setName(request.type());
        role.setMenuPermissions(request.content());
        role.setButtonPermissions(request.content());
        RoleDefinition saved = roleDefinitionMapper.save(role);
        return new SimpleItemResponse(saved.getId(), saved.getName(), saved.getCode(), saved.getMenuPermissions(), "ACTIVE");
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> permissions(int page, int size) {
        Page<SimpleItemResponse> result = permissionItemMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getName(), item.getPermissionType(), item.getCode(), "ACTIVE"));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createPermission(AdminTextRequest request) {
        PermissionItem permission = new PermissionItem();
        permission.setCode(request.title());
        permission.setName(request.content());
        permission.setPermissionType(request.type());
        PermissionItem saved = permissionItemMapper.save(permission);
        return new SimpleItemResponse(saved.getId(), saved.getName(), saved.getPermissionType(), saved.getCode(), "ACTIVE");
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> contentAudits(int page, int size) {
        Page<SimpleItemResponse> result = contentAuditItemMapper.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getTarget(), item.getContentType(), item.getContent(), item.getStatus()));
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createContentAudit(AdminTextRequest request) {
        ContentAuditItem item = new ContentAuditItem();
        item.setTarget(request.title());
        item.setContentType(request.type());
        item.setContent(request.content());
        item.setStatus("PENDING");
        item.setCreatedAt(LocalDateTime.now());
        ContentAuditItem saved = contentAuditItemMapper.save(item);
        return new SimpleItemResponse(saved.getId(), saved.getTarget(), saved.getContentType(),
                saved.getContent(), saved.getStatus());
    }

    @Transactional
    public SimpleItemResponse auditContent(Long id, boolean approved) {
        ContentAuditItem item = contentAuditItemMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Content audit item not found"));
        item.setStatus(approved ? "APPROVED" : "REJECTED");
        ContentAuditItem saved = contentAuditItemMapper.save(item);
        return new SimpleItemResponse(saved.getId(), saved.getTarget(), saved.getContentType(),
                saved.getContent(), saved.getStatus());
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> promotionRules(int page, int size) {
        Page<SimpleItemResponse> result = promotionRuleMapper.findAll(PageRequest.of(page, size)).map(this::toPromotion);
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createPromotionRule(PromotionRuleRequest request) {
        PromotionRule rule = new PromotionRule();
        rule.setTitle(request.title());
        rule.setPromotionType(request.promotionType());
        rule.setThresholdAmount(request.thresholdAmount() == null ? BigDecimal.ZERO : request.thresholdAmount());
        rule.setDiscountAmount(request.discountAmount() == null ? BigDecimal.ZERO : request.discountAmount());
        rule.setRuleText(request.ruleText());
        rule.setStatus("ACTIVE");
        rule.setCreatedAt(LocalDateTime.now());
        return toPromotion(promotionRuleMapper.save(rule));
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> marketingFlows(int page, int size) {
        Page<SimpleItemResponse> result = marketingFlowRecordMapper.findAll(PageRequest.of(page, size)).map(this::toFlow);
        return PageResponse.of(result);
    }

    @Transactional
    public SimpleItemResponse createMarketingFlow(String flowType, AdminTextRequest request) {
        MarketingFlowRecord flow = new MarketingFlowRecord();
        flow.setFlowType(flowType.toUpperCase());
        flow.setTitle(request.title());
        flow.setAmount(parseAmount(request.type()));
        flow.setStatus("RUNNING");
        flow.setDescription(request.content());
        flow.setCreatedAt(LocalDateTime.now());
        return toFlow(marketingFlowRecordMapper.save(flow));
    }

    @Transactional(readOnly = true)
    public List<SimpleItemResponse> analytics(String type) {
        BigDecimal revenue = orderMapper.sumTotalAmountByStatusIn(
                List.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED));
        long products = productMapper.count();
        long orders = orderMapper.count();
        return List.of(
                new SimpleItemResponse(1L, "Revenue", "ANALYTICS", revenue.toPlainString(), type == null ? "ALL" : type),
                new SimpleItemResponse(2L, "Orders", "ANALYTICS", String.valueOf(orders), "CONVERSION"),
                new SimpleItemResponse(3L, "Products", "ANALYTICS", String.valueOf(products), "CATALOG"),
                new SimpleItemResponse(4L, "Traffic Source", "ANALYTICS", "Home 45%, Search 30%, Campaign 25%", "TRAFFIC")
        );
    }

    private BigDecimal parseAmount(String value) {
        try {
            return value == null || value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private SimpleItemResponse toPromotion(PromotionRule rule) {
        return new SimpleItemResponse(rule.getId(), rule.getTitle(), rule.getPromotionType(),
                "threshold=" + rule.getThresholdAmount() + ", discount=" + rule.getDiscountAmount()
                        + ", rule=" + rule.getRuleText(), rule.getStatus());
    }

    private SimpleItemResponse toFlow(MarketingFlowRecord flow) {
        return new SimpleItemResponse(flow.getId(), flow.getTitle(), flow.getFlowType(),
                "amount=" + flow.getAmount() + ", " + flow.getDescription(), flow.getStatus());
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
