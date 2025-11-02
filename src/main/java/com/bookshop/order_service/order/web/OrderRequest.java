package com.bookshop.order_service.order.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.bookshop.order_service.order.domain.OrderItem;
import com.bookshop.order_service.order.domain.OrderStatus;
import com.bookshop.order_service.order.domain.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderRequest {
    Boolean exchange;
    PaymentMethod paymentMethod;
    OrderStatus status;

    String userId;
    String email;
    String phone;
    String firstName;
    String lastName;

    String shippingStreet;
    String shippingCity;
    String shippingState;
    String shippingPostalCode;
    String shippingCountry;

    BigDecimal amount;

    List<OrderItem> orderItems;
    List<String> coupons;
}
