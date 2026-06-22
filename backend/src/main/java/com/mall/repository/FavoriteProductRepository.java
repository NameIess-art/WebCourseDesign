package com.mall.repository;

import com.mall.entity.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<FavoriteProduct> findByUserIdAndProductId(Long userId, Long productId);

    List<FavoriteProduct> findByUserIdOrderByCreatedAtDesc(Long userId);
}
