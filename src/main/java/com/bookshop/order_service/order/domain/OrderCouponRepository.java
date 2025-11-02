package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.order.domain.OrderCoupon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderCouponRepository extends ReactiveCrudRepository<OrderCoupon, Long> {
    Flux<OrderCoupon> findByOrderId(Long orderId);
    Mono<Void> deleteByOrderId(Long orderId);
}