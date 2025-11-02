package com.bookshop.order_service.order.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeliveryEvent {
    Long id;
    Long orderId;

    String userId;
    String firstName;
    String lastName;
    String email;
    String phone;

    String courierFirstName;
    String courierLastName;
    String courierId;
    String courierEmail;
    String courierPhone;

    DeliveryStatus status;
    Boolean exchange;

    String shippingStreet;
    String shippingCity;
    String shippingState;
    String shippingPostalCode;
    String shippingCountry;

    Instant createdDate;
    Instant lastModifiedDate;
}
