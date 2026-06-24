package com.mall.controller;

import com.mall.dto.SeckillEventRequest;
import com.mall.entity.UserAccount;
import com.mall.service.SeckillService;
import com.mall.vo.ApiResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.PageResponse;
import com.mall.vo.SeckillEventResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    @GetMapping("/seckill-events")
    public ApiResponse<PageResponse<SeckillEventResponse>> events(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(seckillService.events(page, size));
    }

    @PostMapping("/admin/seckill-events")
    public ApiResponse<SeckillEventResponse> createEvent(@Valid @RequestBody SeckillEventRequest request) {
        return ApiResponse.success(seckillService.createEvent(request));
    }

    @PostMapping("/seckill-events/{id}/purchase")
    public ApiResponse<OrderResponse> purchase(@AuthenticationPrincipal UserAccount user, @PathVariable Long id) {
        return ApiResponse.success(seckillService.purchase(user, id));
    }
}
