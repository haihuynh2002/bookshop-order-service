package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.order.domain.Coupon;
import com.bookshop.order_service.order.domain.CouponType;
import com.bookshop.order_service.order.domain.OrderCoupon;
import com.bookshop.order_service.order.web.CouponRequest;
import com.bookshop.order_service.order.web.CouponResponse;
import com.bookshop.order_service.order.web.CouponValidationRequest;
import com.bookshop.order_service.order.web.CouponValidationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CouponService {

    CouponRepository couponRepository;
    OrderCouponRepository orderCouponRepository;
    CouponMapper couponMapper;

    public Mono<CouponResponse> createCoupon(CouponRequest request) {
        return couponRepository.existsByCode(request.getCode())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Coupon code already exists"));
                    }
                    Coupon coupon = couponMapper.toCoupon(request);
                    return couponRepository.save(coupon)
                            .map(couponMapper::toCouponResponse);
                });
    }

    public Flux<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .map(couponMapper::toCouponResponse);
    }

    public Mono<CouponResponse> getCouponById(Long id) {
        return couponRepository.findById(id)
                .switchIfEmpty(Mono.error(new CouponNotFoundException("Coupon not found with id: " + id)))
                .map(couponMapper::toCouponResponse);
    }

    public Mono<CouponResponse> getCouponByCode(String code) {
        return couponRepository.findByCodeAndActiveTrue(code)
                .switchIfEmpty(Mono.error(new CouponNotFoundException("Coupon not found with code: " + code)))
                .map(couponMapper::toCouponResponse);
    }

    public Mono<CouponResponse> updateCoupon(Long id, CouponRequest request) {
        return couponRepository.findById(id)
                .map(coupon -> {
                    couponMapper.update(coupon, request);
                    return coupon;
                })
                .flatMap(couponRepository::save)
                .map(couponMapper::toCouponResponse);
    }

    public Mono<Void> deleteCoupon(Long id) {
        return couponRepository.deleteById(id);
    }

    public Mono<CouponValidationResponse> validateCoupon(CouponValidationRequest request) {
        return couponRepository.findByCodeAndActiveTrue(request.getCouponCode())
                .flatMap(coupon -> {
                    String validationMessage = validateCoupon(coupon, request.getOrderAmount());
                    if (validationMessage != null) {
                        return Mono.just(CouponValidationResponse.builder()
                                .valid(false)
                                .message(validationMessage)
                                .discountAmount(BigDecimal.ZERO)
                                .finalAmount(request.getOrderAmount())
                                .build());
                    }

                    BigDecimal discountAmount = calculateDiscount(coupon, request.getOrderAmount());
                    BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);

                    return Mono.just(CouponValidationResponse.builder()
                            .valid(true)
                            .message("Coupon applied successfully")
                            .discountAmount(discountAmount)
                            .finalAmount(finalAmount.max(BigDecimal.ZERO))
                            .build());
                })
                .switchIfEmpty(Mono.just(CouponValidationResponse.builder()
                        .valid(false)
                        .message("Invalid or inactive coupon code")
                        .discountAmount(BigDecimal.ZERO)
                        .finalAmount(request.getOrderAmount())
                        .build()));
    }

//    public Mono<OrderCoupon> applyCouponToOrder(Long orderId, String couponCode, BigDecimal orderAmount) {
//        return couponRepository.findByCodeAndActiveTrue(couponCode)
//                .flatMap(coupon -> {
//                    String validationMessage = validateCoupon(coupon, orderAmount);
//                    if (validationMessage != null) {
//                        return Mono.error(new RuntimeException(validationMessage));
//                    }
//
//                    BigDecimal discountAmount = calculateDiscount(coupon, orderAmount);
//
//                    OrderCoupon orderCoupon = OrderCoupon.builder()
//                            .orderId(orderId)
//                            .couponId(coupon.getId())
//                            .couponCode(coupon.getCode())
//                            .couponType(coupon.getType())
//                            .discountAmount(discountAmount)
//                            .originalAmount(orderAmount)
//                            .build();
//
//                    // Increment usage count
//                    coupon.setUsageCount(coupon.getUsageCount() + 1);
//                    return couponRepository.save(coupon)
//                            .then(orderCouponRepository.save(orderCoupon));
//                });
//    }

    public Flux<OrderCoupon> getOrderCoupons(Long orderId) {
        return orderCouponRepository.findByOrderId(orderId);
    }

    private String validateCoupon(Coupon coupon, BigDecimal orderAmount) {
        Instant now = Instant.now();

        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return "Coupon is not yet valid";
        }

        if (coupon.getValidTo() != null && now.isAfter(coupon.getValidTo())) {
            return "Coupon has expired";
        }

        if (coupon.getMinimumOrderAmount() != null &&
                orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            return String.format("Minimum order amount of %s required", coupon.getMinimumOrderAmount());
        }

        if (coupon.getMaxUsage() != null &&
                coupon.getUsageCount() >= coupon.getMaxUsage()) {
            return "Coupon usage limit exceeded";
        }

        return null;
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        return switch (coupon.getType()) {
            case PERCENTAGE -> orderAmount.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
            case FIXED_AMOUNT -> coupon.getDiscountValue().min(orderAmount);
            case FREE_SHIPPING -> BigDecimal.ZERO; // Handle shipping separately
        };
    }
}