package com.bookshop.order_service.order.web;

import com.bookshop.order_service.order.domain.CouponType;
import com.bookshop.order_service.order.domain.OrderItem;
import com.bookshop.order_service.order.domain.OrderStatus;
import com.bookshop.order_service.order.domain.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class CouponRequest {
    private String code;
    private String description;
    private CouponType type;
    private BigDecimal discountValue;
    private BigDecimal minimumOrderAmount;
    private Integer maxUsage;
    private Instant validFrom;
    private Instant validTo;
    private Boolean active;
}
