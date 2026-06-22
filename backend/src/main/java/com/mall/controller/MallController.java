package com.mall.controller;

import com.mall.dto.CartItemRequest;
import com.mall.dto.CheckoutRequest;
import com.mall.dto.UpdateCartItemRequest;
import com.mall.entity.UserAccount;
import com.mall.service.MallService;
import com.mall.vo.ApiResponse;
import com.mall.vo.CartResponse;
import com.mall.vo.CategoryResponse;
import com.mall.vo.OrderResponse;
import com.mall.vo.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MallController {

    private final MallService mallService;

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.success(mallService.categories());
    }

    @GetMapping("/products")
    public ApiResponse<List<ProductResponse>> products(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(mallService.products(keyword, categoryId));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductResponse> product(@PathVariable Long id) {
        return ApiResponse.success(mallService.product(id));
    }

    @GetMapping("/cart")
    public ApiResponse<CartResponse> cart(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(mallService.cart(user));
    }

    @PostMapping("/cart")
    public ApiResponse<Void> addToCart(@AuthenticationPrincipal UserAccount user,
                                       @Valid @RequestBody CartItemRequest request) {
        mallService.addToCart(user, request);
        return ApiResponse.success();
    }

    @PutMapping("/cart/{cartItemId}")
    public ApiResponse<Void> updateCart(@AuthenticationPrincipal UserAccount user,
                                        @PathVariable Long cartItemId,
                                        @Valid @RequestBody UpdateCartItemRequest request) {
        mallService.updateCartItem(user, cartItemId, request.quantity());
        return ApiResponse.success();
    }

    @DeleteMapping("/cart/{cartItemId}")
    public ApiResponse<Void> deleteCart(@AuthenticationPrincipal UserAccount user,
                                        @PathVariable Long cartItemId) {
        mallService.removeCartItem(user, cartItemId);
        return ApiResponse.success();
    }

    @PostMapping("/orders/checkout")
    public ApiResponse<OrderResponse> checkout(@AuthenticationPrincipal UserAccount user,
                                               @Valid @RequestBody CheckoutRequest request) {
        return ApiResponse.success(mallService.checkout(user, request));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> orders(@AuthenticationPrincipal UserAccount user) {
        return ApiResponse.success(mallService.orders(user));
    }

    @PatchMapping("/orders/{orderId}/pay")
    public ApiResponse<OrderResponse> pay(@AuthenticationPrincipal UserAccount user,
                                          @PathVariable Long orderId) {
        return ApiResponse.success(mallService.pay(user, orderId));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancel(@AuthenticationPrincipal UserAccount user,
                                             @PathVariable Long orderId) {
        return ApiResponse.success(mallService.cancel(user, orderId));
    }

    @PatchMapping("/orders/{orderId}/receive")
    public ApiResponse<OrderResponse> receive(@AuthenticationPrincipal UserAccount user,
                                              @PathVariable Long orderId) {
        return ApiResponse.success(mallService.confirmReceived(user, orderId));
    }
}
