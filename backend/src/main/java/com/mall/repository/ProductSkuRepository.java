package com.mall.repository;

import com.mall.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    List<ProductSku> findByProductIdOrderByIdAsc(Long productId);

    List<ProductSku> findByProductIdInOrderByProductIdAscIdAsc(Collection<Long> productIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update ProductSku s
               set s.stock = s.stock - :quantity
             where s.id = :skuId
               and s.active = true
               and s.stock >= :quantity
            """)
    int decreaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductSku s set s.stock = s.stock + :quantity where s.id = :skuId")
    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
