package com.bookshop.order_service.order.web;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CouponValidationRequest {
    private String couponCode;
    private BigDecimal orderAmount;
}