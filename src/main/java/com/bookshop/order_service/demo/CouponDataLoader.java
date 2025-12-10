package com.bookshop.order_service.order.demo;

import com.bookshop.order_service.order.domain.Coupon;
import com.bookshop.order_service.order.domain.CouponRepository;
import com.bookshop.order_service.order.domain.CouponType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponDataLoader {

    CouponRepository couponRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadCouponTestData() {
        couponRepository.count()
                .flatMap(count -> {
                    if (count == 0) {
                        log.info("No coupons found, loading sample data...");
                        return createSampleCoupons()
                                .flatMap(couponRepository::save)
                                .collectList()
                                .doOnSuccess(coupons ->
                                        log.info("Loaded {} sample coupons", coupons.size()))
                                .then();
                    } else {
                        log.info("Database already contains {} coupons, skipping sample data", count);
                        return Mono.empty();
                    }
                })
                .subscribe(
                        null,
                        error -> log.error("Error loading coupon data: {}", error.getMessage()),
                        () -> log.info("Coupon data loading completed")
                );
    }

    private Flux<Coupon> createSampleCoupons() {
        Instant now = Instant.now();
        Instant monthFromNow = now.plus(30, ChronoUnit.DAYS);
        Instant twoMonthsFromNow = now.plus(60, ChronoUnit.DAYS);
        Instant weekAgo = now.minus(7, ChronoUnit.DAYS);
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        Instant nextWeek = now.plus(7, ChronoUnit.DAYS);
        Instant nextMonth = now.plus(30, ChronoUnit.DAYS);

        List<Coupon> coupons = List.of(
                // Percentage discount coupons
                Coupon.builder()
                        .code("WELCOME10")
                        .description("Welcome discount for new customers")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(10))
                        .minimumOrderAmount(BigDecimal.valueOf(20.00))
                        .maxUsage(1000)
                        .usageCount(0)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                Coupon.builder()
                        .code("SPRING25")
                        .description("Spring sale special discount")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(25))
                        .minimumOrderAmount(BigDecimal.valueOf(50.00))
                        .maxUsage(500)
                        .usageCount(125)
                        .validFrom(now)
                        .validTo(twoMonthsFromNow)
                        .active(true)
                        .build(),

                // Fixed amount discount coupons
                Coupon.builder()
                        .code("FREESHIP")
                        .description("Free shipping on orders")
                        .type(CouponType.FIXED_AMOUNT)
                        .discountValue(BigDecimal.valueOf(5.99))
                        .minimumOrderAmount(BigDecimal.valueOf(25.00))
                        .maxUsage(null)
                        .usageCount(342)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                Coupon.builder()
                        .code("SAVE5")
                        .description("Save $5 on your order")
                        .type(CouponType.FIXED_AMOUNT)
                        .discountValue(BigDecimal.valueOf(5.00))
                        .minimumOrderAmount(BigDecimal.valueOf(30.00))
                        .maxUsage(2000)
                        .usageCount(789)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                // Expired coupon
                Coupon.builder()
                        .code("HOLIDAY2023")
                        .description("Last year's holiday discount")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(15))
                        .minimumOrderAmount(BigDecimal.valueOf(40.00))
                        .maxUsage(1000)
                        .usageCount(999)
                        .validFrom(weekAgo)
                        .validTo(yesterday)
                        .active(false)
                        .build(),

                // Future coupon
                Coupon.builder()
                        .code("SUMMER2024")
                        .description("Upcoming summer sale")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(20))
                        .minimumOrderAmount(BigDecimal.valueOf(35.00))
                        .maxUsage(1500)
                        .usageCount(0)
                        .validFrom(nextWeek)
                        .validTo(nextMonth)
                        .active(true)
                        .build(),

                // No minimum order
                Coupon.builder()
                        .code("BOOKLOVER")
                        .description("Discount for all book lovers")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(5))
                        .minimumOrderAmount(BigDecimal.ZERO)
                        .maxUsage(null)
                        .usageCount(2105)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                // High-value discount
                Coupon.builder()
                        .code("BIG50")
                        .description("Big discount for large orders")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(50))
                        .minimumOrderAmount(BigDecimal.valueOf(100.00))
                        .maxUsage(100)
                        .usageCount(23)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                // Limited usage
                Coupon.builder()
                        .code("VIPONLY")
                        .description("Exclusive offer for VIP customers")
                        .type(CouponType.FIXED_AMOUNT)
                        .discountValue(BigDecimal.valueOf(15.00))
                        .minimumOrderAmount(BigDecimal.valueOf(75.00))
                        .maxUsage(50)
                        .usageCount(48)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build(),

                // Bundle discount
                Coupon.builder()
                        .code("BUNDLE20")
                        .description("Discount when buying multiple books")
                        .type(CouponType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(20))
                        .minimumOrderAmount(BigDecimal.valueOf(60.00))
                        .maxUsage(300)
                        .usageCount(156)
                        .validFrom(now)
                        .validTo(monthFromNow)
                        .active(true)
                        .build()
        );

        return Flux.fromIterable(coupons);
    }
}