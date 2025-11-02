package com.bookshop.order_service.order.web;

import com.bookshop.order_service.order.domain.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CouponResponse> createCoupon(@RequestBody CouponRequest request) {
        return couponService.createCoupon(request);
    }

    @GetMapping
    public Flux<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Mono<CouponResponse> getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @GetMapping("/code/{code}")
    public Mono<CouponResponse> getCouponByCode(@PathVariable String code) {
        return couponService.getCouponByCode(code);
    }

    @PutMapping("/{id}")
    public Mono<CouponResponse> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest request) {
        return couponService.updateCoupon(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCoupon(@PathVariable Long id) {
        return couponService.deleteCoupon(id);
    }

    @PostMapping("/validate")
    public Mono<CouponValidationResponse> validateCoupon(@RequestBody CouponValidationRequest request) {
        return couponService.validateCoupon(request);
    }
//
//    @GetMapping("/order/{orderId}")
//    public Flux<OrderCoupon> getOrderCoupons(@PathVariable Long orderId) {
//        return couponService.getOrderCoupons(orderId);
//    }
}