package com.mall.repository;

import com.mall.entity.ProductQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {

    List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId);
}
