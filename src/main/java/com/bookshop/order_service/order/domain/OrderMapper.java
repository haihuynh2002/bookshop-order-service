package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.order.event.OrderEvent;
import com.bookshop.order_service.order.web.OrderRequest;
import com.bookshop.order_service.order.web.OrderResponse;
import com.bookshop.order_service.order.web.OrderUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import reactor.core.publisher.Mono;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {

    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Order toOrder(Order order);

    OrderEvent toOrderEvent(Order order);

    void updateOrder(@MappingTarget Order order, OrderUpdateRequest request);

    void toOrder(@MappingTarget Order order, OrderRequest request);
}