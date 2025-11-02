package com.bookshop.order_service.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Review {

    Long id;

    String content;

    Integer rating;

    String reviewName;

    Instant createdDate;

    Instant lastModifiedDate;

    String createdBy;

    String lastModifiedBy;

    int version;
}