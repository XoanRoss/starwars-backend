package org.xoanross.starwars.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.xoanross.starwars.backend.exception.model.ClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient swapiHttpClient(@Value("${api.swapi.response-timeout}") Duration responseTimeout,
                                      @Value("${api.swapi.connect-timeout}") Duration connectTimeout) {
        return HttpClient.create()
                .responseTimeout(responseTimeout)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis());
    }

    @Bean
    public WebClient swapiWebClient(@Value("${api.swapi.base-url}") String baseUrl, HttpClient swapiHttpClient) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(swapiHttpClient))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ClientException(
                                        clientResponse.statusCode().value(),
                                        "Error calling client: " + clientResponse.statusCode() + " - " + errorBody)));
                    }
                    return Mono.just(clientResponse);
                }))
                .filter((request, next) -> next.exchange(request)
                        .onErrorMap(ex -> {
                            if (ex instanceof TimeoutException
                                    || ex instanceof ReadTimeoutException
                                    || ex instanceof PrematureCloseException) {
                                return new ClientException(504, "Timeout calling client");
                            }
                            return ex;
                        }))
                .build();
    }
}
