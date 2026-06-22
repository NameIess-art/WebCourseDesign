package com.mall.controller;

import com.mall.dto.PaymentCallbackRequest;
import com.mall.dto.PaymentRequest;
import com.mall.entity.UserAccount;
import com.mall.service.PaymentService;
import com.mall.vo.ApiResponse;
import com.mall.vo.PaymentResponse;
import com.mall.vo.PointRecordResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ApiResponse<PaymentResponse> createPayment(@AuthenticationPrincipal UserAccount user,
                                                      @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.success(paymentService.createPayment(user, request));
    }

    @PostMapping("/payments/callback/mock")
    public ApiResponse<PaymentResponse> mockCallback(@Valid @RequestBody PaymentCallbackRequest request) {
        return ApiResponse.success(paymentService.mockCallback(request));
    }

    @GetMapping("/payments")
    public ApiResponse<List<PaymentResponse>> payments(@AuthenticationPrincipal UserAccount user,
                                                       @RequestParam Long orderId) {
        return ApiResponse.success(paymentService.payments(user, orderId));
    }

    @GetMapping("/member/point-records")
    public ApiResponse<List<PointRecordResponse>> pointRecords(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(paymentService.pointRecords(user));
    }

    @GetMapping("/member/points")
    public ApiResponse<Integer> points(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(user.getPoints() == null ? 0 : user.getPoints());
    }
}
