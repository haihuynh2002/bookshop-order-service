package com.bookshop.order_service.order.domain;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long bookId, Long typeId) {
        super("Inventory not found for Book ID: " + bookId + ", Type ID: " + typeId);
    }
}
