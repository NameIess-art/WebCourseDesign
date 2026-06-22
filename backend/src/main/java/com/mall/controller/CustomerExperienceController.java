package com.mall.controller;

import com.mall.dto.AddressRequest;
import com.mall.dto.AfterSaleRequest;
import com.mall.dto.ProductQuestionRequest;
import com.mall.dto.ProductReviewRequest;
import com.mall.entity.UserAccount;
import com.mall.service.CustomerExperienceService;
import com.mall.vo.AddressResponse;
import com.mall.vo.AfterSaleResponse;
import com.mall.vo.ApiResponse;
import com.mall.vo.CouponResponse;
import com.mall.vo.FavoriteResponse;
import com.mall.vo.LogisticsResponse;
import com.mall.vo.MemberMessageResponse;
import com.mall.vo.MemberProfileResponse;
import com.mall.vo.ProductQuestionResponse;
import com.mall.vo.ProductReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerExperienceController {

    private final CustomerExperienceService customerExperienceService;

    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> addresses(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.addresses(user));
    }

    @PostMapping("/addresses")
    public ApiResponse<AddressResponse> saveAddress(@AuthenticationPrincipal UserAccount user,
                                                    @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(customerExperienceService.saveAddress(user, request));
    }

    @DeleteMapping("/addresses/{id}")
    public ApiResponse<Void> deleteAddress(@AuthenticationPrincipal UserAccount user, @PathVariable Long id) {
        customerExperienceService.deleteAddress(user, id);
        return ApiResponse.success();
    }

    @GetMapping("/coupons")
    public ApiResponse<List<CouponResponse>> coupons(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.coupons(user));
    }

    @PostMapping("/coupons/{id}/claim")
    public ApiResponse<CouponResponse> claimCoupon(@AuthenticationPrincipal UserAccount user, @PathVariable Long id) {
        return ApiResponse.success(customerExperienceService.claimCoupon(user, id));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<FavoriteResponse>> favorites(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.favorites(user));
    }

    @PostMapping("/favorites/{productId}")
    public ApiResponse<Void> favorite(@AuthenticationPrincipal UserAccount user, @PathVariable Long productId) {
        customerExperienceService.favorite(user, productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/favorites/{productId}")
    public ApiResponse<Void> unfavorite(@AuthenticationPrincipal UserAccount user, @PathVariable Long productId) {
        customerExperienceService.unfavorite(user, productId);
        return ApiResponse.success();
    }

    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<List<ProductReviewResponse>> reviews(@PathVariable Long productId) {
        return ApiResponse.success(customerExperienceService.reviews(productId));
    }

    @PostMapping("/products/{productId}/reviews")
    public ApiResponse<ProductReviewResponse> addReview(@AuthenticationPrincipal UserAccount user,
                                                        @PathVariable Long productId,
                                                        @Valid @RequestBody ProductReviewRequest request) {
        return ApiResponse.success(customerExperienceService.addReview(user, productId, request));
    }

    @GetMapping("/products/{productId}/questions")
    public ApiResponse<List<ProductQuestionResponse>> questions(@PathVariable Long productId) {
        return ApiResponse.success(customerExperienceService.questions(productId));
    }

    @PostMapping("/products/{productId}/questions")
    public ApiResponse<ProductQuestionResponse> askQuestion(@AuthenticationPrincipal UserAccount user,
                                                            @PathVariable Long productId,
                                                            @Valid @RequestBody ProductQuestionRequest request) {
        return ApiResponse.success(customerExperienceService.askQuestion(user, productId, request));
    }

    @GetMapping("/member/profile")
    public ApiResponse<MemberProfileResponse> profile(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.profile(user));
    }

    @GetMapping("/member/messages")
    public ApiResponse<List<MemberMessageResponse>> messages(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.messages(user));
    }

    @PatchMapping("/member/messages/{id}/read")
    public ApiResponse<Void> readMessage(@AuthenticationPrincipal UserAccount user, @PathVariable Long id) {
        customerExperienceService.readMessage(user, id);
        return ApiResponse.success();
    }

    @GetMapping("/orders/{orderId}/logistics")
    public ApiResponse<LogisticsResponse> logistics(@AuthenticationPrincipal UserAccount user,
                                                    @PathVariable Long orderId) {
        return ApiResponse.success(customerExperienceService.logistics(user, orderId));
    }

    @PostMapping("/orders/{orderId}/after-sales")
    public ApiResponse<AfterSaleResponse> requestAfterSale(@AuthenticationPrincipal UserAccount user,
                                                           @PathVariable Long orderId,
                                                           @Valid @RequestBody AfterSaleRequest request) {
        return ApiResponse.success(customerExperienceService.requestAfterSale(user, orderId, request));
    }

    @GetMapping("/after-sales")
    public ApiResponse<List<AfterSaleResponse>> afterSales(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(customerExperienceService.afterSales(user));
    }
}
