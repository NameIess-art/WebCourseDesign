package com.mall.vo;

public record AddressResponse(
        Long id,
        String receiver,
        String phone,
        String region,
        String detail,
        Boolean defaultAddress
) {
}
