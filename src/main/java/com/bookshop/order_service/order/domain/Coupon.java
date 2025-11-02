// Coupon.java
package com.bookshop.order_service.order.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table("coupon")
public class Coupon {

    @Id
    Long id;

    String code;
    String description;
    CouponType type;
    BigDecimal discountValue;
    BigDecimal minimumOrderAmount;
    Integer maxUsage;
    Integer usageCount;
    Instant validFrom;
    Instant validTo;
    Boolean active;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant lastModifiedDate;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @Version
    int version;
}
