package com.mall.controller;

import com.mall.dto.ConfigRequest;
import com.mall.dto.MarketingActivityRequest;
import com.mall.service.OperationService;
import com.mall.vo.ApiResponse;
import com.mall.vo.HighConcurrencyResponse;
import com.mall.vo.MarketingActivityResponse;
import com.mall.vo.OperationBoardResponse;
import com.mall.vo.PlatformRiskResponse;
import com.mall.vo.ReportResponse;
import com.mall.vo.SystemConfigResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/operation-board")
    public ApiResponse<OperationBoardResponse> board() {
        return ApiResponse.success(operationService.board());
    }

    @GetMapping("/marketing")
    public ApiResponse<List<MarketingActivityResponse>> activities() {
        return ApiResponse.success(operationService.activities());
    }

    @PostMapping("/marketing")
    public ApiResponse<MarketingActivityResponse> createActivity(@Valid @RequestBody MarketingActivityRequest request) {
        return ApiResponse.success(operationService.createActivity(request));
    }

    @GetMapping("/configs")
    public ApiResponse<List<SystemConfigResponse>> configs() {
        return ApiResponse.success(operationService.configs());
    }

    @PutMapping("/configs/{key}")
    public ApiResponse<SystemConfigResponse> upsertConfig(@PathVariable String key,
                                                          @Valid @RequestBody ConfigRequest request) {
        return ApiResponse.success(operationService.upsertConfig(key, request));
    }

    @GetMapping("/risk")
    public ApiResponse<List<PlatformRiskResponse>> risks() {
        return ApiResponse.success(operationService.risks());
    }

    @PatchMapping("/risk/{id}/resolve")
    public ApiResponse<PlatformRiskResponse> resolveRisk(@PathVariable Long id) {
        return ApiResponse.success(operationService.resolveRisk(id));
    }

    @GetMapping("/reports")
    public ApiResponse<ReportResponse> reports() {
        return ApiResponse.success(operationService.reports());
    }

    @GetMapping("/high-concurrency")
    public ApiResponse<HighConcurrencyResponse> highConcurrency() {
        return ApiResponse.success(operationService.highConcurrency());
    }
}
