package com.mall.controller;

import com.mall.dto.AdminTextRequest;
import com.mall.dto.ConfigRequest;
import com.mall.dto.MarketingActivityRequest;
import com.mall.dto.PromotionRuleRequest;
import com.mall.service.OperationService;
import com.mall.vo.ApiResponse;
import com.mall.vo.HighConcurrencyResponse;
import com.mall.vo.MarketingActivityResponse;
import com.mall.vo.OperationBoardResponse;
import com.mall.vo.PlatformRiskResponse;
import com.mall.vo.ReportResponse;
import com.mall.vo.SimpleItemResponse;
import com.mall.vo.SystemConfigResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PatchMapping("/activities/{id}/audit")
    public ApiResponse<MarketingActivityResponse> auditActivity(@PathVariable Long id,
                                                                @RequestParam boolean approved) {
        return ApiResponse.success(operationService.auditActivity(id, approved));
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

    @PostMapping("/configs/{key}")
    public ApiResponse<SystemConfigResponse> createConfig(@PathVariable String key,
                                                          @Valid @RequestBody ConfigRequest request) {
        return ApiResponse.success(operationService.upsertConfig(key, request));
    }

    @DeleteMapping("/configs/{key}")
    public ApiResponse<Void> deleteConfig(@PathVariable String key) {
        operationService.deleteConfig(key);
        return ApiResponse.success();
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

    @GetMapping("/dictionaries")
    public ApiResponse<List<SimpleItemResponse>> dictionaries() {
        return ApiResponse.success(operationService.dictionaries());
    }

    @PostMapping("/dictionaries")
    public ApiResponse<SimpleItemResponse> createDictionary(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createDictionary(request));
    }

    @DeleteMapping("/dictionaries/{id}")
    public ApiResponse<Void> deleteDictionary(@PathVariable Long id) {
        operationService.deleteDictionary(id);
        return ApiResponse.success();
    }

    @GetMapping("/announcements")
    public ApiResponse<List<SimpleItemResponse>> announcements() {
        return ApiResponse.success(operationService.announcements());
    }

    @PostMapping("/announcements")
    public ApiResponse<SimpleItemResponse> createAnnouncement(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createAnnouncement(request));
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        operationService.deleteAnnouncement(id);
        return ApiResponse.success();
    }

    @GetMapping("/merchants")
    public ApiResponse<List<SimpleItemResponse>> merchants() {
        return ApiResponse.success(operationService.merchants());
    }

    @PostMapping("/merchants")
    public ApiResponse<SimpleItemResponse> createMerchant(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createMerchant(request));
    }

    @PatchMapping("/merchants/{id}")
    public ApiResponse<SimpleItemResponse> updateMerchantStatus(@PathVariable Long id,
                                                                @RequestParam String status) {
        return ApiResponse.success(operationService.updateMerchantStatus(id, status));
    }

    @PostMapping("/merchants/{id}/penalties")
    public ApiResponse<SimpleItemResponse> penalizeMerchant(@PathVariable Long id,
                                                            @Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.penalizeMerchant(id, request));
    }

    @GetMapping("/roles")
    public ApiResponse<List<SimpleItemResponse>> roles() {
        return ApiResponse.success(operationService.roles());
    }

    @PostMapping("/roles")
    public ApiResponse<SimpleItemResponse> createRole(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createRole(request));
    }

    @GetMapping("/permissions")
    public ApiResponse<List<SimpleItemResponse>> permissions() {
        return ApiResponse.success(operationService.permissions());
    }

    @PostMapping("/permissions")
    public ApiResponse<SimpleItemResponse> createPermission(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createPermission(request));
    }

    @GetMapping("/content-audits")
    public ApiResponse<List<SimpleItemResponse>> contentAudits() {
        return ApiResponse.success(operationService.contentAudits());
    }

    @PostMapping("/content-audits")
    public ApiResponse<SimpleItemResponse> createContentAudit(@Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createContentAudit(request));
    }

    @PatchMapping("/content-audits/{id}/audit")
    public ApiResponse<SimpleItemResponse> auditContent(@PathVariable Long id,
                                                        @RequestParam boolean approved) {
        return ApiResponse.success(operationService.auditContent(id, approved));
    }

    @GetMapping("/promotion-rules")
    public ApiResponse<List<SimpleItemResponse>> promotionRules() {
        return ApiResponse.success(operationService.promotionRules());
    }

    @PostMapping("/promotion-rules")
    public ApiResponse<SimpleItemResponse> createPromotionRule(@Valid @RequestBody PromotionRuleRequest request) {
        return ApiResponse.success(operationService.createPromotionRule(request));
    }

    @GetMapping("/marketing/flows")
    public ApiResponse<List<SimpleItemResponse>> marketingFlows() {
        return ApiResponse.success(operationService.marketingFlows());
    }

    @PostMapping("/marketing/{flowType}")
    public ApiResponse<SimpleItemResponse> createMarketingFlow(@PathVariable String flowType,
                                                               @Valid @RequestBody AdminTextRequest request) {
        return ApiResponse.success(operationService.createMarketingFlow(flowType, request));
    }

    @GetMapping("/analytics")
    public ApiResponse<List<SimpleItemResponse>> analytics(@RequestParam(required = false) String type) {
        return ApiResponse.success(operationService.analytics(type));
    }
}
