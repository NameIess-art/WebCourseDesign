package com.mall.controller;

import com.mall.dto.AfterSaleProcessRequest;
import com.mall.dto.OrderAuditRequest;
import com.mall.dto.OrderModifyRequest;
import com.mall.dto.ProductRequest;
import com.mall.service.AdminService;
import com.mall.vo.AfterSaleResponse;
import com.mall.vo.ApiResponse;
import com.mall.vo.DashboardResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.ProductResponse;
import com.mall.vo.SimpleItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.success(adminService.dashboard());
    }

    @GetMapping("/products")
    public ApiResponse<List<ProductResponse>> products() {
        return ApiResponse.success(adminService.products());
    }

    @PostMapping("/products")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(adminService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(adminService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ApiResponse.success();
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> orders() {
        return ApiResponse.success(adminService.orders());
    }

    @PatchMapping("/orders/{orderId}/ship")
    public ApiResponse<OrderResponse> ship(@PathVariable Long orderId) {
        return ApiResponse.success(adminService.shipOrder(orderId));
    }

    @PatchMapping("/orders/{orderId}/audit")
    public ApiResponse<OrderResponse> audit(@PathVariable Long orderId,
                                            @Valid @RequestBody OrderAuditRequest request) {
        return ApiResponse.success(adminService.auditOrder(orderId, request));
    }

    @PatchMapping("/orders/{orderId}/modify")
    public ApiResponse<OrderResponse> modify(@PathVariable Long orderId,
                                             @Valid @RequestBody OrderModifyRequest request) {
        return ApiResponse.success(adminService.modifyOrder(orderId, request));
    }

    @PostMapping("/reconciliation")
    public ApiResponse<SimpleItemResponse> reconcile(@RequestParam(required = false) java.time.LocalDate bizDate) {
        var record = adminService.reconcile(bizDate);
        return ApiResponse.success(new SimpleItemResponse(record.getId(), record.getBizDate().toString(),
                record.getStatus(), "orders=" + record.getOrderAmount() + ", payments=" + record.getPaymentAmount(),
                record.getStatus()));
    }

    @GetMapping("/reconciliation")
    public ApiResponse<List<SimpleItemResponse>> reconciliations() {
        return ApiResponse.success(adminService.reconciliations());
    }

    @GetMapping("/after-sales")
    public ApiResponse<List<AfterSaleResponse>> afterSales() {
        return ApiResponse.success(adminService.allAfterSales());
    }

    @PatchMapping("/after-sales/{id}/process")
    public ApiResponse<AfterSaleResponse> processAfterSale(@PathVariable Long id,
                                                           @Valid @RequestBody AfterSaleProcessRequest request) {
        return ApiResponse.success(adminService.processAfterSale(id, request));
    }
}
