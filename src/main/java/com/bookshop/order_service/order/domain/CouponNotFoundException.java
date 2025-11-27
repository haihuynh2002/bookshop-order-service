package com.bookshop.order_service.order.domain;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long id) {
        super("Coupon not found with id: " + id);
    }

    public CouponNotFoundException(String code) {
        super("Coupon not found with code: " + code);
    }
}
