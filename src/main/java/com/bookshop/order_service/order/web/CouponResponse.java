package com.bookshop.order_service.order.web;

import com.bookshop.order_service.order.domain.CouponType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String description;
    private CouponType type;
    private BigDecimal discountValue;
    private BigDecimal minimumOrderAmount;
    private Integer maxUsage;
    private Integer usageCount;
    private Instant validFrom;
    private Instant validTo;
    private Boolean active;
    private Instant createdDate;
    private Instant lastModifiedDate;
}