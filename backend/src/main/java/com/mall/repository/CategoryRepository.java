package com.mall.repository;

import com.mall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderBySortOrderAscIdAsc();
    Page<Category> findAllByOrderBySortOrderAscIdAsc(Pageable pageable);
}
