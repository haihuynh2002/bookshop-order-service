package com.bookshop.order_service.order.domain;

public class CouponAlreadyExistsException extends RuntimeException {
    public CouponAlreadyExistsException(String code) {
        super("Coupon already exists with code: " + code);
    }
}