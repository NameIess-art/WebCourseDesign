package com.mall.service;

import com.mall.dto.CartItemRequest;
import com.mall.dto.CheckoutRequest;
import com.mall.entity.CartItem;
import com.mall.entity.IdempotentRecord;
import com.mall.entity.OrderEntity;
import com.mall.entity.OrderItem;
import com.mall.entity.PointRecord;
import com.mall.entity.Product;
import com.mall.entity.ProductDetailBlock;
import com.mall.entity.ProductSku;
import com.mall.entity.UserAccount;
import com.mall.entity.UserCoupon;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.mapper.CartItemMapper;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.IdempotentRecordMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.PointRecordMapper;
import com.mall.mapper.ProductDetailBlockMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.ProductSkuMapper;
import com.mall.mapper.UserCouponMapper;
import com.mall.mapper.UserMapper;
import com.mall.vo.CartItemResponse;
import com.mall.vo.CartResponse;
import com.mall.vo.CategoryResponse;
import com.mall.vo.OrderItemResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.PageResponse;
import com.mall.vo.ProductDetailBlockResponse;
import com.mall.vo.ProductResponse;
import com.mall.vo.ProductSkuResponse;
import com.mall.vo.RecommendationResponse;
import com.mall.vo.SearchSuggestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MallService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final CartItemMapper cartItemMapper;
    private final OrderMapper orderMapper;
    private final UserCouponMapper userCouponMapper;
    private final ProductSkuMapper productSkuMapper;
    private final ProductDetailBlockMapper productDetailBlockMapper;
    private final PointRecordMapper pointRecordMapper;
    private final IdempotentRecordMapper idempotentRecordMapper;
    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> categories(int page, int size) {
        Page<CategoryResponse> result = categoryMapper.findAllByOrderBySortOrderAscIdAsc(PageRequest.of(page, size))
                .map(category -> new CategoryResponse(category.getId(), category.getName()));
        return PageResponse.of(result);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> products(String keyword, Long categoryId) {
        return products(keyword, categoryId, 0, 200).content();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> products(String keyword, Long categoryId, int page, int size) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        Pageable pageable = pageRequest(page, size);
        Page<Product> products;
        if (categoryId != null && hasKeyword) {
            products = productMapper.findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(
                    categoryId, keyword.trim(), pageable);
        } else if (categoryId != null) {
            products = productMapper.findByActiveTrueAndCategoryIdOrderByIdDesc(categoryId, pageable);
        } else if (hasKeyword) {
            products = productMapper.findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(keyword.trim(), pageable);
        } else {
            products = productMapper.findByActiveTrueOrderByIdDesc(pageable);
        }
        List<Long> productIds = products.getContent().stream().map(Product::getId).toList();
        Map<Long, List<ProductSku>> skuMap = productIds.isEmpty()
                ? Map.of()
                : productSkuMapper.findByProductIdInOrderByProductIdAscIdAsc(productIds).stream()
                .collect(Collectors.groupingBy(sku -> sku.getProduct().getId()));
        Page<ProductResponse> responsePage = products.map(product -> toProductResponse(
                product,
                skuMap.getOrDefault(product.getId(), List.of()),
                List.of()
        ));
        return PageResponse.of(responsePage);
    }

    @Transactional(readOnly = true)
    public SearchSuggestResponse suggest(String keyword) {
        String value = keyword == null ? "" : keyword.trim();
        List<String> productNames = (value.isBlank()
                ? productMapper.findByActiveTrueOrderByIdDesc(PageRequest.of(0, 8)).getContent()
                : productMapper.findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(
                value, PageRequest.of(0, 8)).getContent())
                .stream()
                .map(Product::getName)
                .toList();
        List<String> categories = categoryMapper.findAllByOrderBySortOrderAscIdAsc().stream()
                .filter(category -> value.isBlank()
                        || category.getName().toLowerCase().contains(value.toLowerCase()))
                .map(category -> category.getName())
                .toList();
        return new SearchSuggestResponse(productNames, categories);
    }

    @Transactional(readOnly = true)
    public RecommendationResponse recommendations() {
        List<Product> products = productMapper.findByActiveTrueOrderByIdDesc(PageRequest.of(0, 50)).getContent();
        List<Long> productIds = products.stream().map(Product::getId).toList();
        Map<Long, List<ProductSku>> skuMap = productIds.isEmpty()
                ? Map.of()
                : productSkuMapper.findByProductIdInOrderByProductIdAscIdAsc(productIds).stream()
                .collect(Collectors.groupingBy(sku -> sku.getProduct().getId()));
        List<ProductResponse> all = products.stream()
                .map(product -> toProductResponse(product, skuMap.getOrDefault(product.getId(), List.of()), List.of()))
                .toList();
        List<ProductResponse> hot = all.stream()
                .sorted(Comparator.comparing(ProductResponse::sales,
                        Comparator.nullsLast(Integer::compareTo)).reversed())
                .limit(4)
                .toList();
        List<ProductResponse> latest = all.stream().limit(4).toList();
        List<ProductResponse> activity = all.stream()
                .filter(item -> item.promotionTag() != null && !item.promotionTag().isBlank())
                .limit(4)
                .toList();
        return new RecommendationResponse(hot, latest, activity);
    }

    @Transactional(readOnly = true)
    public ProductResponse product(Long id) {
        Product product = getProduct(id);
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Product is unavailable");
        }
        return toProductResponse(product, true);
    }

    /**
     * 添加商品到购物车
     * 处理用户将商品加入购物车的逻辑，校验商品状态和库存，并更新购物车数量
     * @param user 当前登录用户
     * @param request 包含商品ID、SKU ID以及数量的请求
     */
    @Transactional
    public void addToCart(UserAccount user, CartItemRequest request) {
        // 根据传入的商品 ID 获取商品实体对象
        Product product = getProduct(request.productId());
        // 校验商品是否处于上架有效状态
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Product is unavailable");
        }
        // 解析出用户所选择的具体规格 (SKU)
        ProductSku sku = resolveSku(product, request.skuId());
        // 校验当前所选规格的库存是否满足加入购物车的数量
        if (sku.getStock() < request.quantity()) {
            throw new BusinessException("Insufficient SKU stock");
        }

        // 查询购物车中是否已经存在相同的商品及规格
        CartItem cartItem = cartItemMapper.findByUserIdAndProductIdAndSkuId(
                user.getId(), product.getId(), sku.getId()).orElseGet(() -> {
            // 若不存在，则初始化一条新的购物车记录
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setSku(sku);
            item.setQuantity(0);
            return item;
        });

        // 计算叠加后的购物车目标数量
        int targetQuantity = cartItem.getQuantity() + request.quantity();
        // 再次校验叠加后的总数是否超出库存
        if (targetQuantity > sku.getStock()) {
            throw new BusinessException("Cart quantity exceeds SKU stock");
        }
        // 更新购物车商品数量并保存到数据库
        cartItem.setQuantity(targetQuantity);
        cartItemMapper.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse cart(UserAccount user) {
        List<CartItemResponse> items = cartItemMapper.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(CartItem::getId).reversed())
                .map(item -> {
                    ProductSku sku = item.getSku();
                    Product product = item.getProduct();
                    BigDecimal price = sku == null ? product.getPrice() : sku.getPrice();
                    int stock = sku == null ? product.getStock() : sku.getStock();
                    BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponse(
                            item.getId(),
                            product.getId(),
                            sku == null ? null : sku.getId(),
                            sku == null ? product.getSpec() : sku.getSpecName(),
                            product.getName(),
                            product.getImageUrl(),
                            price,
                            stock,
                            item.getQuantity(),
                            subtotal
                    );
                })
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, total);
    }

    @Transactional
    public void updateCartItem(UserAccount user, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemMapper.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Cart item not found"));
        if (quantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
        int stock = cartItem.getSku() == null ? cartItem.getProduct().getStock() : cartItem.getSku().getStock();
        if (stock < quantity) {
            throw new BusinessException("Insufficient stock");
        }
        cartItem.setQuantity(quantity);
        cartItemMapper.save(cartItem);
    }

    @Transactional
    public void removeCartItem(UserAccount user, Long cartItemId) {
        CartItem cartItem = cartItemMapper.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Cart item not found"));
        cartItemMapper.delete(cartItem);
    }

    /**
     * 购物车结账与订单生成
     * 将用户购物车中的所有商品结算，扣减库存，应用优惠并生成对应的订单
     * @param user 当前登录用户
     * @param request 结账请求对象，包含收货地址、优惠券、使用积分等信息
     * @return 订单生成结果响应
     */
    @Transactional
    public OrderResponse checkout(UserAccount user, CheckoutRequest request) {
        // 利用幂等键防止网络延迟或重试导致的重复下单
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                && idempotentRecordMapper.existsByIdemKey(request.idempotencyKey())) {
            // 如果幂等记录已存在，直接返回之前已创建的对应订单，避免重复生成
            return orderMapper.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                    .filter(order -> request.idempotencyKey().equals(order.getIdempotencyKey()))
                    .findFirst()
                    .map(this::toOrderResponse)
                    .orElseThrow(() -> new BusinessException("Duplicate checkout request"));
        }

        // 从数据库中获取用户当前购物车的所有商品条目
        List<CartItem> cartItems = cartItemMapper.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        // 初始化主订单实体，并生成唯一的业务订单号
        OrderEntity order = new OrderEntity();
        order.setOrderNo("MALL" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setUser(user);
        order.setShippingAddress(request.shippingAddress());
        order.setStatus(OrderStatus.PENDING_PAYMENT); // 初始状态设为待付款
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setIdempotencyKey(request.idempotencyKey());

        BigDecimal total = BigDecimal.ZERO;
        // 遍历购物车内的每一项商品，构建订单的明细行并扣减库存
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            ProductSku sku = cartItem.getSku() == null ? resolveSku(product, null) : cartItem.getSku();
            
            // 尝试在数据库层面通过乐观锁原子递减库存，确保并发场景下不会超卖
            if (productSkuMapper.decreaseStock(sku.getId(), cartItem.getQuantity()) == 0
                    || productMapper.decreaseStock(product.getId(), cartItem.getQuantity()) == 0) {
                throw new BusinessException(product.getName() + " is out of stock");
            }

            // 初始化具体的订单明细(订单子项)，关联商品及购买的数量和单价
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setSku(sku);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(sku.getPrice());
            order.getItems().add(orderItem);

            // 累加计算订单的商品总金额
            total = total.add(sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // 计算优惠券所抵扣的折扣金额
        BigDecimal discount = couponDiscount(user, request.couponId(), total);
        
        // 校验并计算会员积分所抵扣的金额
        int pointsUsed = request.pointsUsed() == null ? 0 : Math.max(0, request.pointsUsed());
        int availablePoints = user.getPoints() == null ? 0 : user.getPoints();
        if (pointsUsed > availablePoints) {
            throw new BusinessException("Not enough member points");
        }
        BigDecimal pointsDiscount = BigDecimal.valueOf(pointsUsed)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
        BigDecimal maxPointsDiscount = total.subtract(discount).max(BigDecimal.ZERO);
        if (pointsDiscount.compareTo(maxPointsDiscount) > 0) {
            pointsDiscount = maxPointsDiscount;
        }
        
        // 如果使用了积分，则扣除用户余额并生成对应的积分变动流水
        if (pointsUsed > 0) {
            user.setPoints(availablePoints - pointsUsed);
            pointRecordMapper.save(pointRecord(user, -pointsUsed, "USE", "Order points deduction"));
        }

        // 回填各类计算好的金额到主订单实体
        order.setOriginalAmount(total);
        order.setDiscountAmount(discount);
        order.setPointsUsed(pointsUsed);
        order.setPointsDiscountAmount(pointsDiscount);
        order.setTotalAmount(total.subtract(discount).subtract(pointsDiscount).max(BigDecimal.ZERO));
        
        // 持久化保存主订单数据
        OrderEntity saved = orderMapper.save(order);
        
        // 级联保存所有订单明细项
        for (OrderItem item : order.getItems()) {
            item.setOrder(saved);
            orderItemMapper.insert(item);
        }

        // 保存本次操作的幂等记录
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            IdempotentRecord record = new IdempotentRecord();
            record.setIdemKey(request.idempotencyKey());
            record.setBizType("ORDER_CHECKOUT");
            record.setBizResult(saved.getOrderNo());
            record.setCreatedAt(LocalDateTime.now());
            idempotentRecordMapper.save(record);
        }

        // 订单生成成功后，清空用户当前的购物车数据
        cartItemMapper.deleteByUserId(user.getId());
        return toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> orders(UserAccount user) {
        return orders(user, 0, 200).content();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> orders(UserAccount user, int page, int size) {
        return PageResponse.of(orderMapper.findByUserIdOrderByCreatedAtDesc(user.getId(), pageRequest(page, size))
                .map(this::toOrderResponse));
    }

    @Transactional
    public OrderResponse pay(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("Order cannot be paid in its current state");
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentChannel("BALANCE");
        int earned = order.getTotalAmount().intValue();
        user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + earned);
        userMapper.save(user);
        pointRecordMapper.save(pointRecord(user, earned, "EARN", "Order payment reward"));
        return toOrderResponse(orderMapper.save(order));
    }

    @Transactional
    public OrderResponse cancel(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getItems()) {
            if (item.getSku() != null) {
                productSkuMapper.restoreStock(item.getSku().getId(), item.getQuantity());
            }
            productMapper.restoreStock(item.getProduct().getId(), item.getQuantity());
        }
        return toOrderResponse(orderMapper.save(order));
    }

    @Transactional
    public OrderResponse confirmReceived(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("Only shipped orders can be completed");
        }
        order.setStatus(OrderStatus.COMPLETED);
        return toOrderResponse(orderMapper.save(order));
    }

    private BigDecimal couponDiscount(UserAccount user, Long couponId, BigDecimal total) {
        if (couponId == null) {
            return BigDecimal.ZERO;
        }
        UserCoupon userCoupon = userCouponMapper.findByUserIdOrderByClaimedAtDesc(user.getId()).stream()
                .filter(item -> item.getCoupon().getId().equals(couponId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Coupon not claimed"));
        if (Boolean.TRUE.equals(userCoupon.getUsed())) {
            throw new BusinessException("Coupon already used");
        }
        if (total.compareTo(userCoupon.getCoupon().getThresholdAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        userCoupon.setUsed(true);
        return userCoupon.getCoupon().getDiscountAmount();
    }

    private Product getProduct(Long id) {
        return productMapper.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
    }

    private ProductSku resolveSku(Product product, Long skuId) {
        ProductSku sku = skuId == null
                ? productSkuMapper.findByProductIdOrderByIdAsc(product.getId()).stream().findFirst()
                .orElseThrow(() -> new BusinessException("SKU not found"))
                : productSkuMapper.findById(skuId)
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .orElseThrow(() -> new BusinessException("SKU not found"));
        if (!Boolean.TRUE.equals(sku.getActive())) {
            throw new BusinessException("SKU is unavailable");
        }
        return sku;
    }

    private OrderEntity getUserOrder(UserAccount user, Long orderId) {
        return orderMapper.findById(orderId)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Order not found"));
    }

    private ProductResponse toProductResponse(Product product, boolean includeDetailBlocks) {
        List<ProductSku> skus = productSkuMapper.findByProductIdOrderByIdAsc(product.getId());
        List<ProductDetailBlock> blocks = includeDetailBlocks
                ? productDetailBlockMapper.findByProductIdOrderBySortOrderAscIdAsc(product.getId())
                : List.of();
        return toProductResponse(product, skus, blocks);
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

    private PointRecord pointRecord(UserAccount user, Integer points, String type, String description) {
        PointRecord record = new PointRecord();
        record.setUser(user);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    private Pageable pageRequest(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
    }
}
