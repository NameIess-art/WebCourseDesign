package com.mall.repository;

import com.mall.entity.ProductDetailBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductDetailBlockRepository extends JpaRepository<ProductDetailBlock, Long> {

    List<ProductDetailBlock> findByProductIdOrderBySortOrderAscIdAsc(Long productId);

    List<ProductDetailBlock> findByProductIdInOrderByProductIdAscSortOrderAscIdAsc(Collection<Long> productIds);
}
