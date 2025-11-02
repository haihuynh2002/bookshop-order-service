package com.bookshop.order_service.order.domain;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table("order_item")
public class OrderItem {
    @Id
    Long id;
    Long orderId;
    Long bookId;

    String isbn;
    String title;
    String author;
    String publisher;
    BigDecimal price;
    Integer quantity;

    Long typeId;
    String typeName;
}
