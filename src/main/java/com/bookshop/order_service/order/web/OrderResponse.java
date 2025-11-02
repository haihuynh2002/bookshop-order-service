package com.bookshop.order_service.order.web;

import com.bookshop.order_service.order.domain.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponse {
    Long id;

    List<OrderItem> orderItems;
    List<Coupon> coupons;
    Double amount;

    Boolean exchange;
    PaymentMethod paymentMethod;
    OrderStatus status;

    String userId;
    String email;
    String phone;
    String firstName;
    String lastName;

    String billingStreet;
    String billingCity;
    String billingState;
    String billingPostalCode;
    String billingCountry;

    String shippingStreet;
    String shippingCity;
    String shippingState;
    String shippingPostalCode;
    String shippingCountry;

    Instant createdDate;
    Instant lastModifiedDate;
    String createdBy;
    String lastModifiedBy;
    int version;
}


