package com.bookshop.order_service.order.event;

import com.bookshop.order_service.order.domain.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentEvent {
    Long id;
    Long orderId;
    String paymentId;

    String token;
    BigDecimal amount;

    String userId;
    String email;
    String firstName;
    String lastName;

    PaymentMethod paymentMethod;
    PaymentStatus status;

    String billingStreet;
    String billingCity;
    String billingState;
    String billingPostalCode;
    String billingCountry;

    Instant createdDate;
    Instant lastModifiedDate;
}