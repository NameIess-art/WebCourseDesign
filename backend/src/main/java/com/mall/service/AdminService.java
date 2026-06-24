package com.mall.service;

import com.mall.dto.AfterSaleProcessRequest;
import com.mall.dto.OrderAuditRequest;
import com.mall.dto.OrderModifyRequest;
import com.mall.dto.ProductRequest;
import com.mall.entity.AfterSaleRecord;
import com.mall.entity.Category;
import com.mall.entity.OrderAuditRecord;
import com.mall.entity.OrderEntity;
import com.mall.entity.OrderModifyRecord;
import com.mall.entity.PaymentRecord;
import com.mall.entity.Product;
import com.mall.entity.ProductDetailBlock;
import com.mall.entity.ProductSku;
import com.mall.entity.ReconciliationRecord;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.AfterSaleRecordRepository;
import com.mall.repository.CategoryRepository;
import com.mall.repository.OrderAuditRecordRepository;
import com.mall.repository.OrderModifyRecordRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.PaymentRecordRepository;
import com.mall.repository.ProductDetailBlockRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.ProductSkuRepository;
import com.mall.repository.ReconciliationRecordRepository;
import com.mall.repository.UserRepository;
import com.mall.repository.MerchantRepository;
import com.mall.vo.AfterSaleResponse;
import com.mall.vo.DashboardResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.PageResponse;
import com.mall.vo.ProductDetailBlockResponse;
import com.mall.vo.ProductResponse;
import com.mall.vo.ProductSkuResponse;
import com.mall.vo.SimpleItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductDetailBlockRepository productDetailBlockRepository;
    private final OrderAuditRecordRepository orderAuditRecordRepository;
    private final OrderModifyRecordRepository orderModifyRecordRepository;
    private final ReconciliationRecordRepository reconciliationRecordRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final AfterSaleRecordRepository afterSaleRecordRepository;
    private final MerchantRepository merchantRepository;

    private Long requireMerchantId() {
        Long merchantId = com.mall.context.MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException("Please select a merchant first");
        }
        return merchantId;
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        Long merchantId = requireMerchantId();
        List<OrderStatus> revenueStatuses = List.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED);
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByMerchantIdAndStatusIn(merchantId, revenueStatuses);

        return new DashboardResponse(
                userRepository.countByMerchantId(merchantId),
                productRepository.countByMerchantId(merchantId),
                orderRepository.countByMerchantId(merchantId),
                orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_PAYMENT),
                totalRevenue
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> products() {
        return products(0, 500).content();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> products(int page, int size) {
        Long merchantId = requireMerchantId();
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        var productPage = productRepository.findByMerchantIdOrderByIdDesc(merchantId, PageRequest.of(safePage, safeSize));
        List<Long> productIds = productPage.getContent().stream().map(Product::getId).toList();
        Map<Long, List<ProductSku>> skuMap = productIds.isEmpty()
                ? Map.of()
                : productSkuRepository.findByProductIdInOrderByProductIdAscIdAsc(productIds).stream()
                .collect(Collectors.groupingBy(sku -> sku.getProduct().getId()));
        Map<Long, List<ProductDetailBlock>> detailMap = productIds.isEmpty()
                ? Map.of()
                : productDetailBlockRepository.findByProductIdInOrderByProductIdAscSortOrderAscIdAsc(productIds).stream()
                .collect(Collectors.groupingBy(block -> block.getProduct().getId()));
        return PageResponse.of(productPage.map(product -> toProductResponse(
                product,
                skuMap.getOrDefault(product.getId(), List.of()),
                detailMap.getOrDefault(product.getId(), List.of())
        )));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Long merchantId = requireMerchantId();
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));
        Product product = new Product();
        product.setMerchant(merchantRepository.findById(merchantId).orElseThrow());
        updateFields(product, request, category);
        Product saved = productRepository.save(product);
        syncProductChildren(saved, request);
        return toProductResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));
        updateFields(product, request, category);
        Product saved = productRepository.save(product);
        syncProductChildren(saved, request);
        return toProductResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Only paid orders can be shipped");
        }
        order.setStatus(OrderStatus.SHIPPED);
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> orders() {
        return orders(null, 0, 500).content();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> orders(String status, int page, int size) {
        Long merchantId = requireMerchantId();
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        PageRequest pageRequest = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (status == null || status.isBlank()) {
            return PageResponse.of(orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId, pageRequest).map(this::toOrderResponse));
        }
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.trim().toUpperCase());
            return PageResponse.of(orderRepository.findByMerchantIdAndStatusOrderByCreatedAtDesc(merchantId, orderStatus, pageRequest)
                    .map(this::toOrderResponse));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid order status");
        }
    }

    @Transactional
    public OrderResponse auditOrder(Long orderId, OrderAuditRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));
        OrderAuditRecord record = new OrderAuditRecord();
        record.setOrder(order);
        record.setApproved(request.approved());
        record.setRemark(request.remark());
        record.setCreatedAt(LocalDateTime.now());
        orderAuditRecordRepository.save(record);
        order.setAuditStatus(Boolean.TRUE.equals(request.approved()) ? "APPROVED" : "REJECTED");
        order.setMerchantRemark(request.remark());
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse modifyOrder(Long orderId, OrderModifyRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Only unpaid or paid orders can be modified");
        }
        OrderModifyRecord record = new OrderModifyRecord();
        record.setOrder(order);
        record.setBeforeAmount(order.getTotalAmount());
        record.setAfterAmount(request.totalAmount() == null ? order.getTotalAmount() : request.totalAmount());
        record.setBeforeAddress(order.getShippingAddress());
        record.setAfterAddress(request.shippingAddress());
        record.setRemark(request.remark());
        record.setCreatedAt(LocalDateTime.now());
        orderModifyRecordRepository.save(record);
        order.setShippingAddress(request.shippingAddress());
        if (request.totalAmount() != null) {
            order.setTotalAmount(request.totalAmount());
        }
        order.setMerchantRemark(request.remark());
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public ReconciliationRecord reconcile(LocalDate bizDate) {
        LocalDate date = bizDate == null ? LocalDate.now() : bizDate;
        LocalDateTime startAt = date.atStartOfDay();
        LocalDateTime endAt = date.plusDays(1).atStartOfDay();
        long paidOrderCount = orderRepository.countPaidOrdersBetween(startAt, endAt);
        BigDecimal orderAmount = orderRepository.sumPaidOrderAmountBetween(startAt, endAt);
        BigDecimal paymentAmount = paymentRecordRepository.sumPaidAmountBetween(startAt, endAt);
        ReconciliationRecord record = reconciliationRecordRepository.findByBizDate(date)
                .orElseGet(ReconciliationRecord::new);
        record.setBizDate(date);
        record.setOrderCount(paidOrderCount);
        record.setOrderAmount(orderAmount);
        record.setPaymentAmount(paymentAmount);
        record.setStatus(orderAmount.compareTo(paymentAmount) == 0 ? "MATCHED" : "DIFF");
        return reconciliationRecordRepository.save(record);
    }

    @Transactional
    public AfterSaleResponse processAfterSale(Long id, AfterSaleProcessRequest request) {
        AfterSaleRecord record = afterSaleRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("After-sale record not found"));
        record.setStatus(request.status());
        AfterSaleRecord saved = afterSaleRecordRepository.save(record);
        return new AfterSaleResponse(saved.getId(), saved.getOrder().getOrderNo(), saved.getType(),
                saved.getReason() + "; process remark: " + request.remark(), saved.getStatus(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public PageResponse<AfterSaleResponse> afterSales(int page, int size) {
        Page<AfterSaleResponse> result = afterSaleRecordRepository.findAll(PageRequest.of(page, size)).map(this::toAfterSaleResponse);
        return PageResponse.of(result);
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleItemResponse> reconciliations(int page, int size) {
        Page<SimpleItemResponse> result = reconciliationRecordRepository.findAll(PageRequest.of(page, size)).map(item -> new SimpleItemResponse(item.getId(), item.getBizDate().toString(), "RECONCILIATION",
                        "Total: " + item.getOrderAmount() + " / Payments: " + item.getPaymentAmount(),
                        item.getStatus()));
        return PageResponse.of(result);
    }

    private AfterSaleResponse toAfterSaleResponse(AfterSaleRecord record) {
        return new AfterSaleResponse(record.getId(), record.getOrder().getOrderNo(),
                        record.getType(), record.getReason(), record.getStatus(), record.getCreatedAt());
    }

    private void updateFields(Product product, ProductRequest request, Category category) {
        product.setName(request.name());
        product.setSubtitle(request.subtitle());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);
        product.setActive(request.active());
        if (product.getSales() == null) {
            product.setSales(0);
        }
        product.setSkuCode(request.skuCode() == null || request.skuCode().isBlank()
                ? "SKU-" + System.currentTimeMillis() : request.skuCode());
        product.setSpec(request.spec() == null || request.spec().isBlank() ? "Standard" : request.spec());
        product.setPromotionTag(request.promotionTag() == null || request.promotionTag().isBlank()
                ? "Platform Pick" : request.promotionTag());
        if (product.getFavoriteCount() == null) {
            product.setFavoriteCount(0);
        }
        if (product.getQuestionCount() == null) {
            product.setQuestionCount(0);
        }
        if (product.getRating() == null) {
            product.setRating(new BigDecimal("4.80"));
        }
    }

    private void syncProductChildren(Product product, ProductRequest request) {
        productSkuRepository.deleteAll(productSkuRepository.findByProductIdOrderByIdAsc(product.getId()));
        List<ProductSku> skus = request.skus() == null || request.skus().isEmpty()
                ? List.of(defaultSku(product))
                : request.skus().stream().map(item -> {
                    ProductSku sku = new ProductSku();
                    sku.setProduct(product);
                    sku.setSkuCode(item.skuCode());
                    sku.setSpecName(item.specName());
                    sku.setPrice(item.price());
                    sku.setStock(item.stock());
                    sku.setActive(item.active() == null || item.active());
                    return sku;
                }).toList();
        productSkuRepository.saveAll(skus);

        productDetailBlockRepository.deleteAll(
                productDetailBlockRepository.findByProductIdOrderBySortOrderAscIdAsc(product.getId()));
        List<ProductDetailBlock> blocks = request.detailBlocks() == null || request.detailBlocks().isEmpty()
                ? List.of(defaultDetailBlock(product))
                : request.detailBlocks().stream().map(item -> {
                    ProductDetailBlock block = new ProductDetailBlock();
                    block.setProduct(product);
                    block.setBlockType(item.blockType());
                    block.setContent(item.content());
                    block.setSortOrder(item.sortOrder() == null ? 0 : item.sortOrder());
                    return block;
                }).toList();
        productDetailBlockRepository.saveAll(blocks);
    }

    private ProductSku defaultSku(Product product) {
        ProductSku sku = new ProductSku();
        sku.setProduct(product);
        sku.setSkuCode(product.getSkuCode());
        sku.setSpecName(product.getSpec());
        sku.setPrice(product.getPrice());
        sku.setStock(product.getStock());
        sku.setActive(product.getActive());
        return sku;
    }

    private ProductDetailBlock defaultDetailBlock(Product product) {
        ProductDetailBlock block = new ProductDetailBlock();
        block.setProduct(product);
        block.setBlockType("TEXT");
        block.setContent(product.getDescription() == null || product.getDescription().isBlank()
                ? product.getSubtitle() : product.getDescription());
        block.setSortOrder(0);
        return block;
    }

    private ProductResponse toProductResponse(Product product) {
        return toProductResponse(
                product,
                productSkuRepository.findByProductIdOrderByIdAsc(product.getId()),
                productDetailBlockRepository.findByProductIdOrderBySortOrderAscIdAsc(product.getId())
        );
    }

    private ProductResponse toProductResponse(Product product, List<ProductSku> skus, List<ProductDetailBlock> blocks) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock(),
                product.getSales(),
                product.getActive(),
                product.getSkuCode(),
                product.getSpec(),
                product.getPromotionTag(),
                product.getFavoriteCount(),
                product.getQuestionCount(),
                product.getRating(),
                skus.stream()
                        .map(sku -> new ProductSkuResponse(sku.getId(), sku.getSkuCode(), sku.getSpecName(),
                                sku.getPrice(), sku.getStock(), sku.getActive()))
                        .toList(),
                blocks.stream()
                        .map(block -> new ProductDetailBlockResponse(block.getId(), block.getBlockType(),
                                block.getContent(), block.getSortOrder()))
                        .toList(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    private OrderResponse toOrderResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getOriginalAmount() == null ? BigDecimal.ZERO : order.getOriginalAmount(),
                order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount(),
                order.getPointsUsed() == null ? 0 : order.getPointsUsed(),
                order.getPointsDiscountAmount() == null ? BigDecimal.ZERO : order.getPointsDiscountAmount(),
                order.getPaymentChannel(),
                order.getAuditStatus() == null ? "PENDING" : order.getAuditStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new com.mall.vo.OrderItemResponse(
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
