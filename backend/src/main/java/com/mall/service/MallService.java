package com.mall.service;

import com.mall.dto.CartItemRequest;
import com.mall.dto.CheckoutRequest;
import com.mall.entity.CartItem;
import com.mall.entity.OrderEntity;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.entity.UserAccount;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.CartItemRepository;
import com.mall.repository.CategoryRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.vo.CartItemResponse;
import com.mall.vo.CartResponse;
import com.mall.vo.CategoryResponse;
import com.mall.vo.OrderItemResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MallService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> categories() {
        return categoryRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> products(String keyword, Long categoryId) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        List<Product> products;
        if (categoryId != null && hasKeyword) {
            products = productRepository.findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(
                    categoryId, keyword.trim());
        } else if (categoryId != null) {
            products = productRepository.findByActiveTrueAndCategoryIdOrderByIdDesc(categoryId);
        } else if (hasKeyword) {
            products = productRepository.findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(keyword.trim());
        } else {
            products = productRepository.findByActiveTrueOrderByIdDesc();
        }
        return products.stream().map(this::toProductResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse product(Long id) {
        Product product = getProduct(id);
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Product is unavailable");
        }
        return toProductResponse(product);
    }

    @Transactional
    public void addToCart(UserAccount user, CartItemRequest request) {
        Product product = getProduct(request.productId());
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Product is unavailable");
        }
        if (product.getStock() < request.quantity()) {
            throw new BusinessException("Insufficient stock");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product).orElseGet(() -> {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(0);
            return item;
        });

        int targetQuantity = cartItem.getQuantity() + request.quantity();
        if (targetQuantity > product.getStock()) {
            throw new BusinessException("Cart quantity exceeds stock");
        }
        cartItem.setQuantity(targetQuantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse cart(UserAccount user) {
        List<CartItemResponse> items = cartItemRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(CartItem::getId).reversed())
                .map(item -> {
                    BigDecimal subtotal = item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponse(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getImageUrl(),
                            item.getProduct().getPrice(),
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
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Cart item not found"));
        if (quantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
        if (cartItem.getProduct().getStock() < quantity) {
            throw new BusinessException("Insufficient stock");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(UserAccount user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public OrderResponse checkout(UserAccount user, CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setOrderNo("MALL" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setUser(user);
        order.setShippingAddress(request.shippingAddress());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (productRepository.decreaseStock(product.getId(), cartItem.getQuantity()) == 0) {
                throw new BusinessException(product.getName() + " is out of stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total);
        OrderEntity saved = orderRepository.save(order);
        cartItemRepository.deleteByUserId(user.getId());
        return toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> orders(UserAccount user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional
    public OrderResponse pay(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("Order cannot be paid in its current state");
        }
        order.setStatus(OrderStatus.PAID);
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancel(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getItems()) {
            productRepository.restoreStock(item.getProduct().getId(), item.getQuantity());
        }
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse confirmReceived(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("Only shipped orders can be completed");
        }
        order.setStatus(OrderStatus.COMPLETED);
        return toOrderResponse(orderRepository.save(order));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
    }

    private OrderEntity getUserOrder(UserAccount user, Long orderId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Order not found"));
    }

    private ProductResponse toProductResponse(Product product) {
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
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList()
        );
    }
}
