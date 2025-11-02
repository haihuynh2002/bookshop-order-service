package com.bookshop.order_service.order.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ExchangeEvent {
    Long id;
    Long orderId;
    String condition;
    String reason;
    ExchangeStatus status;

    Instant createdDate;
    Instant lastModifiedDate;
    String createdBy;
    String lastModifiedBy;
}
