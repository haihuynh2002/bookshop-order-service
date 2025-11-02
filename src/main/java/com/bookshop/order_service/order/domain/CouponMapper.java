package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.order.web.CouponRequest;
import com.bookshop.order_service.order.web.CouponResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CouponMapper {

    Coupon toCoupon(CouponRequest request);

    CouponResponse toCouponResponse(Coupon Coupon);

    void update(@MappingTarget Coupon Coupon, CouponRequest request);
}