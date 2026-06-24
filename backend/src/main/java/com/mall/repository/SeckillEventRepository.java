package com.mall.repository;

import com.mall.entity.SeckillEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeckillEventRepository extends JpaRepository<SeckillEvent, Long> {

    List<SeckillEvent> findByActiveTrueOrderByStartAtDesc();
    Page<SeckillEvent> findByActiveTrueOrderByStartAtDesc(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SeckillEvent e set e.stock = e.stock - 1, e.sold = e.sold + 1 where e.id = :eventId and e.active = true and e.stock > 0")
    int decreaseStock(@Param("eventId") Long eventId);
}
