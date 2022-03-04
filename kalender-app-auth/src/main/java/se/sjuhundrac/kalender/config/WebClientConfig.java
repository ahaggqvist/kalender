package se.sjuhundrac.kalender.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@RequiredArgsConstructor
@Configuration
class WebClientConfig {
    @NonNull
    private final AppProperties appProperties;

    @Profile("prod")
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(appProperties.getApiUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Profile("!prod")
    @Bean("webClient")
    public WebClient insecureWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(appProperties.getApiUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .secure(
                                                t -> {
                                                    try {
                                                        t.sslContext(
                                                                SslContextBuilder.forClient()
                                                                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                                                        .build());
                                                    } catch (SSLException e) {
                                                        log.error(e.getMessage(), e);
                                                    }
                                                })))
                .build();
    }
}
