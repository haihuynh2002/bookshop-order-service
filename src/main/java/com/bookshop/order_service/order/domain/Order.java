package com.bookshop.order_service.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.annotation.Generated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table("orders")
public class Order {

	@Id
	Long id;

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

    BigDecimal amount;

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
