package com.mall.repository;

import com.mall.entity.ProductQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {

    List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId);
    Page<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
