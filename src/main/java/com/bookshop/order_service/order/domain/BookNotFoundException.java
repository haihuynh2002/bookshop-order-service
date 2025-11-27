package com.bookshop.order_service.order.domain;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book not found with id: " + bookId);
    }
}