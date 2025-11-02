package com.bookshop.order_service.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
public class BookTypeResponse {
    Long bookId;
    String title;
    String author;
    String isbn;

    Long typeId;
    String name;

    Integer quantity;
}
