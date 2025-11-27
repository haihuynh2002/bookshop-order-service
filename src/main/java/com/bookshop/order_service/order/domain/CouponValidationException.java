package com.bookshop.order_service.order.domain;

public class CouponValidationException extends RuntimeException {
    public CouponValidationException(String message) {
        super("Coupon validation failed: " + message);
    }
}
