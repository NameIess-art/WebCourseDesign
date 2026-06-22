package com.mall.repository;

import com.mall.entity.ContentAuditItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentAuditItemRepository extends JpaRepository<ContentAuditItem, Long> {
}
