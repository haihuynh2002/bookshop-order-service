package com.bookshop.order_service.order.web;

import com.bookshop.order_service.order.domain.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateRequest {
    OrderStatus status;
}
