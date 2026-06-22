package com.mall.repository;

import com.mall.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByIdDesc();

    List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(String keyword);

    List<Product> findByActiveTrueAndCategoryIdOrderByIdDesc(Long categoryId);

    List<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(Long categoryId, String keyword);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
               set p.stock = p.stock - :quantity,
                   p.sales = p.sales + :quantity
             where p.id = :productId
               and p.active = true
               and p.stock >= :quantity
            """)
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
               set p.stock = p.stock + :quantity,
                   p.sales = case when p.sales >= :quantity then p.sales - :quantity else 0 end
             where p.id = :productId
            """)
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
