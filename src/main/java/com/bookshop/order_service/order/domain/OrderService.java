package com.bookshop.order_service.order.domain;

import com.bookshop.order_service.book.BookClient;
import com.bookshop.order_service.book.BookTypeResponse;
import com.bookshop.order_service.book.BookTypeUpdateRequest;
import com.bookshop.order_service.book.InventoryClient;
import com.bookshop.order_service.order.event.*;
import com.bookshop.order_service.order.web.OrderRequest;
import com.bookshop.order_service.order.web.OrderResponse;
import com.bookshop.order_service.order.web.OrderUpdateRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

    CouponRepository couponRepository;
    OrderItemRepository orderItemRepository;
    BookClient bookClient;
    InventoryClient inventoryClient;
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    StreamBridge streamBridge;
    OrderCouponRepository orderCouponRepository;

    public Flux<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .map(orderMapper::toOrderResponse)
                .flatMap(this::enrichOrderWithItems)
                .flatMap(this::enrichOrderWithCoupons);
    }

    public Flux<OrderResponse> getMyOrders(String userId) {
        return orderRepository.findByCreatedBy(userId)
                .map(orderMapper::toOrderResponse)
                .flatMap(this::enrichOrderWithItems);
    }

    public Mono<OrderResponse> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .map(orderMapper::toOrderResponse)
                .flatMap(this::enrichOrderWithItems);
    }

    @Transactional
    public Mono<OrderResponse> submitOrder(OrderRequest request, Jwt jwt) {
        return createAndSaveOrder(request, jwt)
                .flatMap(order -> validateAndUpdateInventory(request)
                        .collectList()
                        .thenReturn(order))
                .doOnNext(this::publishOrderEvent)
                .flatMap(this::enrichOrderResponse);
    }

    private Flux<BookTypeResponse> validateAndUpdateInventory(OrderRequest request) {
        return Flux.fromIterable(request.getOrderItems())
                .flatMap(this::validateAndUpdateItemInventory);
    }

    private Mono<BookTypeResponse> validateAndUpdateItemInventory(OrderItem item) {
        return inventoryClient.getInventoryItem(item.getBookId(), item.getTypeId())
                .switchIfEmpty(Mono.error(new InventoryNotFoundException(item.getBookId(), item.getTypeId())))
                .flatMap(inventory -> validateStock(item, inventory))
                .flatMap(inventory -> updateInventory(item, inventory))
                .doOnNext(response -> log.info("Updated inventory: {}", response));
    }

    private Mono<BookTypeResponse> validateStock(OrderItem item, BookTypeResponse inventory) {
        if (inventory.getQuantity() < item.getQuantity()) {
            return Mono.error(new InsufficientStockException(item.getBookId(), item.getTypeId(), item.getQuantity(), inventory.getQuantity()));
        }
        return Mono.just(inventory);
    }

    private Mono<BookTypeResponse> updateInventory(OrderItem item, BookTypeResponse inventory) {
        int newQuantity = inventory.getQuantity() - item.getQuantity();
        BookTypeUpdateRequest updateRequest = BookTypeUpdateRequest.builder()
                .quantity(newQuantity)
                .build();

        return inventoryClient.updateInventory(item.getBookId(), item.getTypeId(), updateRequest)
                .doOnNext(response -> log.info("Inventory update response: {}", response));
    }

    private Mono<Order> createAndSaveOrder(OrderRequest request, Jwt jwt) {
        return Mono.fromCallable(() -> buildAcceptedOrder(request, jwt))
                .flatMap(orderRepository::save)
                .flatMap(savedOrder -> saveOrderDetails(savedOrder, request));
    }

    private Mono<Order> saveOrderDetails(Order order, OrderRequest request) {
        return saveOrderItems(order, request.getOrderItems())
                .then(saveOrderCoupons(order, request.getCoupons()))
                .thenReturn(order);
    }

    private Mono<OrderResponse> enrichOrderResponse(Order order) {
        return orderItemRepository.findAllByOrderId(order.getId())
                .collectList()
                .map(items -> {
                    OrderResponse response = orderMapper.toOrderResponse(order);
                    response.setOrderItems(items);
                    return response;
                });
    }

    private Mono<OrderResponse> enrichOrderWithItems(OrderResponse orderResponse) {
        return orderItemRepository.findAllByOrderId(orderResponse.getId())
                .collectList()
                .map(items -> {
                    orderResponse.setOrderItems(items);
                    return orderResponse;
                });
    }

    private Mono<OrderResponse> enrichOrderWithCoupons(OrderResponse orderResponse) {
        return orderCouponRepository.findByOrderId(orderResponse.getId())
                .map(OrderCoupon::getCouponId)
                .flatMap(couponRepository::findById)
                .collectList()
                .map(coupons -> {
                    orderResponse.setCoupons(coupons);
                    return orderResponse;
                });
    }

    @Transactional
    public Mono<Void> deleteOrder(Long orderId, String userId) {
        return orderRepository.existsByIdAndCreatedBy(orderId, userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new OrderAccessDeniedException(orderId, userId));
                    }
                    return orderItemRepository.deleteAllByOrderId(orderId)
                            .then(orderCouponRepository.deleteAllByOrderId(orderId))
                            .then(orderRepository.deleteById(orderId));
                });
    }

    public Mono<Order> updateOrder(Long orderId, OrderUpdateRequest request) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .map(order -> {
                    orderMapper.updateOrder(order, request);
                    return order;
                })
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderEvent);
    }

    public Flux<Order> consumeExchangeEvent(Flux<ExchangeEvent> flux) {
        return flux
                .flatMap(event -> orderRepository.findById(event.getOrderId())
                .map(order -> switch (event.getStatus()) {
                    case ExchangeStatus.APPROVED -> exchangeOrder(order);
                    default -> order;
                }))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderEvent);
    }

    public Flux<Order> consumeDeliveryEvent(Flux<DeliveryEvent> flux) {
        return flux
                .flatMap(event -> orderRepository.findById(event.getOrderId())
                        .map(order -> switch (event.getStatus()) {
                            case DeliveryStatus.SHIPPED -> completeOrder(order);
                            case DeliveryStatus.CANCELLED -> cancelOrder(order);
                            case DeliveryStatus.ASSIGNED -> shipOrder(order);
                            default -> order;
                        }))
                .flatMap(orderRepository::save);
    }

    public Flux<Order> consumePaymentEvent(Flux<PaymentEvent> flux) {
        return flux
                .flatMap(event -> orderRepository.findById(event.getOrderId())
                        .map(order -> switch (event.getStatus()) {
                            case PaymentStatus.CANCELLED -> cancelOrder(order);
                            case PaymentStatus.COMPLETED -> paidOrder(order);
                            default -> order;
                        }))
                .flatMap(orderRepository::save);
    }

    private void publishOrderEvent(Order order) {
        var orderEvent = orderMapper.toOrderEvent(order);
        var result = streamBridge.send("order-out-0", orderEvent);
        log.info("Order event published for order {}: {}", order.getId(), result);
    }

    private Mono<Void> saveOrderItems(Order order, List<OrderItem> orderItems) {
        return Flux.fromIterable(orderItems)
                .flatMap(item -> bookClient.getBookById(item.getBookId())
                        .switchIfEmpty(Mono.error(new BookNotFoundException(item.getBookId())))
                        .map(book -> {
                            item.setOrderId(order.getId());
                            item.setBookId(book.getId());
                            item.setIsbn(book.getIsbn());
                            item.setTitle(book.getTitle());
                            item.setPrice(book.getPrice());
                            item.setAuthor(book.getAuthor());
                            item.setPublisher(book.getPublisher());
                            return item;
                        }))
                .flatMap(orderItemRepository::save)
                .then();
    }

    private Mono<Void> saveOrderCoupons(Order order, List<String> coupons) {
        return Flux.fromIterable(coupons)
                .flatMap(couponId -> couponRepository.findById(Long.parseLong(couponId))
                        .switchIfEmpty(Mono.error(new CouponNotFoundException(Long.parseLong(couponId))))
                        .flatMap(coupon -> updateCouponCount(coupon))
                        .map(coupon -> OrderCoupon.builder()
                                .orderId(order.getId())
                                .couponId(coupon.getId())
                                .build()))
                .flatMap(orderCouponRepository::save)
                .then();
    }

    private Mono<Coupon> updateCouponCount(Coupon coupon) {
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        return couponRepository.save(coupon);
    }

    private Order buildAcceptedOrder(OrderRequest request, Jwt jwt) {
        var order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);
        order.setUserId(jwt.getSubject());
        order.setFirstName(jwt.getClaim(StandardClaimNames.GIVEN_NAME));
        order.setLastName(jwt.getClaim(StandardClaimNames.FAMILY_NAME));
        order.setEmail(jwt.getClaim(StandardClaimNames.EMAIL));
        order.setPhone(jwt.getClaim(StandardClaimNames.PHONE_NUMBER));
        orderMapper.toOrder(order, request);
        return order;
    }

    private Order completeOrder(Order order) {
        order.setStatus(OrderStatus.COMPLETED);
        return order;
    }

    private Order cancelOrder(Order order) {
        if(order.getExchange()) {
            order.setStatus(OrderStatus.COMPLETED);
            order.setExchange(false);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }
        return order;
    }

    private Order paidOrder(Order order) {
        order.setStatus(OrderStatus.PAID);
        return order;
    }

    private Order shipOrder(Order order) {
        order.setStatus(OrderStatus.SHIPPING);
        return order;
    }

    private Order exchangeOrder(Order order) {
        order.setStatus(OrderStatus.PAID);
        order.setExchange(true);
        return order;
    }
}