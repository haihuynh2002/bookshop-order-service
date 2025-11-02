package com.bookshop.order_service.order.event;

import java.util.function.Consumer;

import com.bookshop.order_service.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFunctions {

	private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

	@Bean
	public Consumer<Flux<DeliveryEvent>> handleDeliveryEvent(OrderService orderService) {
		return flux -> orderService.consumeDeliveryEvent(flux)
				.doOnNext(order -> log.info("Handle delivery event with order: {}", order.getId()))
				.subscribe();
	}

    @Bean
    public Consumer<Flux<ExchangeEvent>> handleExchangeEvent(OrderService orderService) {
        return flux -> orderService.consumeExchangeEvent(flux)
                .doOnNext(order -> log.info("Handle exchange event with order: {}", order.getId()))
                .subscribe();
    }

    @Bean
    public Consumer<Flux<PaymentEvent>> handlePaymentEvent(OrderService orderService) {
        return flux -> orderService.consumePaymentEvent(flux)
                .doOnNext(order -> log.info("Handle payment event with order: {}", order.getId()))
                .subscribe();
    }
}
