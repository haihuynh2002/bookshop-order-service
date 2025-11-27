package com.bookshop.order_service.order.domain;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long bookId, Long typeId, Integer requested, Integer available) {
        super("Insufficient stock for Book ID: " + bookId + ", Type ID: " + typeId +
                ". Requested: " + requested + ", Available: " + available);
    }
}
