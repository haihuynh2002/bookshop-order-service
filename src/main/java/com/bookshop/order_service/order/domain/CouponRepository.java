package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.order.domain.Coupon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CouponRepository extends ReactiveCrudRepository<Coupon, Long> {
    Mono<Coupon> findByCodeAndActiveTrue(String code);
    Mono<Boolean> existsByCode(String code);
}