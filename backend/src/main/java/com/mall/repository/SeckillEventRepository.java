package com.mall.repository;

import com.mall.entity.SeckillEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeckillEventRepository extends JpaRepository<SeckillEvent, Long> {

    List<SeckillEvent> findByActiveTrueOrderByStartAtDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SeckillEvent e set e.stock = e.stock - 1, e.sold = e.sold + 1 where e.id = :eventId and e.active = true and e.stock > 0")
    int decreaseStock(@Param("eventId") Long eventId);
}
