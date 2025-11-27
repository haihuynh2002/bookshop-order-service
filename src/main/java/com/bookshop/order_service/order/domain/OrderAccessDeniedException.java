package com.bookshop.order_service.order.domain;

public class OrderAccessDeniedException extends RuntimeException {
    public OrderAccessDeniedException(Long orderId, String userId) {
        super("Order access denied for order id: " + orderId + " and user id: " + userId);
    }
}