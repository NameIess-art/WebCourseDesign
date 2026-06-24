package com.mall.context;

public class MerchantContextHolder {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static void setMerchantId(Long merchantId) {
        CONTEXT.set(merchantId);
    }

    public static Long getMerchantId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
