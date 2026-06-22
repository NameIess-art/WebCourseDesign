package com.mall.service;

import com.mall.dto.ProductRequest;
import com.mall.entity.Category;
import com.mall.entity.OrderEntity;
import com.mall.entity.Product;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.CategoryRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import com.mall.vo.DashboardResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public DashboardResponse dashboard() {
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID
                        || order.getStatus() == OrderStatus.SHIPPED
                        || order.getStatus() == OrderStatus.COMPLETED)
                .map(OrderEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(
                userRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT),
                totalRevenue
        );
    }

    public List<ProductResponse> products() {
        return productRepository.findAll().stream().map(this::toProductResponse).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));
        Product product = new Product();
        updateFields(product, request, category);
        return toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));
        updateFields(product, request, category);
        return toProductResponse(productRepository.save(product));
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
        OrderEntity saved = orderRepository.save(order);
        return new OrderResponse(
                saved.getId(),
                saved.getOrderNo(),
                saved.getStatus().name(),
                saved.getTotalAmount(),
                saved.getShippingAddress(),
                saved.getCreatedAt(),
                saved.getItems().stream()
                        .map(item -> new com.mall.vo.OrderItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList()
        );
    }

    public List<OrderResponse> orders() {
        return orderRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getOrderNo(),
                        order.getStatus().name(),
                        order.getTotalAmount(),
                        order.getShippingAddress(),
                        order.getCreatedAt(),
                        order.getItems().stream()
                                .map(item -> new com.mall.vo.OrderItemResponse(
                                        item.getId(),
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getPrice()
                                ))
                                .toList()
                ))
                .toList();
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
        if (product.getSkuCode() == null || product.getSkuCode().isBlank()) {
            product.setSkuCode("SKU-" + System.currentTimeMillis());
        }
        if (product.getSpec() == null || product.getSpec().isBlank()) {
            product.setSpec("Standard");
        }
        if (product.getPromotionTag() == null || product.getPromotionTag().isBlank()) {
            product.setPromotionTag("Platform Pick");
        }
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
}
