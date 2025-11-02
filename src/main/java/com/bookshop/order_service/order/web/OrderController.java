package com.bookshop.order_service.order.web;


import com.bookshop.order_service.order.domain.Order;
import com.bookshop.order_service.order.domain.OrderService;
import com.bookshop.order_service.order.domain.OrderStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

    @GetMapping("/{orderId}")
    public Mono<OrderResponse> getOrder(@PathVariable Long orderId,
                                @AuthenticationPrincipal Jwt jwt) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping
    public Flux<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

	@GetMapping("/my-orders")
	public Flux<OrderResponse> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
		return orderService.getMyOrders(jwt.getSubject());
	}

	@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
	public Mono<OrderResponse> submitOrder(@RequestBody @Valid OrderRequest orderRequest,
                                   @AuthenticationPrincipal Jwt jwt) {
        log.info(orderRequest.toString());
		return orderService.submitOrder(orderRequest, jwt);
	}

    @PutMapping("/{orderId}")
    public Mono<Order> updateOrder(@PathVariable Long orderId,
                                   @RequestBody OrderUpdateRequest request) {
        log.info(request.toString());
        return orderService.updateOrder(orderId, request);
    }

    @DeleteMapping("/{orderId}")
    public Mono<Void> deleteOrder(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {
        return orderService.deleteOrder(orderId, jwt.getSubject());
    }


}
