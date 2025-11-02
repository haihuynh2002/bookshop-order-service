package com.bookshop.order_service.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.print.Book;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Category {
    Long id;

    String name;

    String description;

    Instant createdDate;

    Instant lastModifiedDate;
}