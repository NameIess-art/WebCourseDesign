package com.mall.repository;

import com.mall.entity.CartItem;
import com.mall.entity.Product;
import com.mall.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserAndProduct(UserAccount user, Product product);

    Optional<CartItem> findByUserIdAndProductIdAndSkuId(Long userId, Long productId, Long skuId);

    void deleteByUserId(Long userId);
}
