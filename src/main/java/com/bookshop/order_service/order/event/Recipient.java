package com.bookshop.order_service.order.event;

public record Recipient(
        String name,
        String email
) {
}
