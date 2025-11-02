package com.bookshop.order_service.order.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveCrudRepository<Order,Long> {
    Flux<Order> findByCreatedBy(String userId);
    Mono<Boolean> existsByIdAndCreatedBy(Long id, String userId);
}
