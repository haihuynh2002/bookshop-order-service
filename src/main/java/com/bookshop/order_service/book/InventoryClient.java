package com.bookshop.order_service.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class InventoryClient {

	private static final String INVENTORY_ROOT_API = "/inventories/";
	private final WebClient webClient;

	public InventoryClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<BookTypeResponse> getInventoryItem(Long bookId, Long typeId) {
		return webClient
				.get()
				.uri(String.format("%sbook/%s/type/%s", INVENTORY_ROOT_API, bookId, typeId))
				.retrieve()
				.bodyToMono(BookTypeResponse.class)
				.timeout(Duration.ofSeconds(3), Mono.empty())
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
	}

    public Mono<BookTypeResponse> updateInventory(Long bookId, Long typeId, BookTypeUpdateRequest request) {
        return webClient
                .put()
                .uri(String.format("%sbook/%s/type/%s", INVENTORY_ROOT_API, bookId, typeId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BookTypeResponse.class)
                .timeout(Duration.ofSeconds(3), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
    }

}
