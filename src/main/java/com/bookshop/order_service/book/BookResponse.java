package com.bookshop.order_service.book;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookResponse {
    Long id;
    String isbn;
    String title;
    String author;
    BigDecimal price;
    Set<Category> categories;
    Set<Review> reviews;

    String publisher;

    Set<FileResponse> images;

    Instant createdDate;

    Instant lastModifiedDate;

    String createdBy;

    String lastModifiedBy;
}