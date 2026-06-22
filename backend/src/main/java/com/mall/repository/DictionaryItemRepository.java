package com.mall.repository;

import com.mall.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long> {
}
