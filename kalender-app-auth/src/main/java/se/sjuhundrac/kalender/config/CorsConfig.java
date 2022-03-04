package se.sjuhundrac.kalender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static java.util.List.of;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
                of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(
                of(
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "Content-Type",
                        "Authorization",
                        "Credentials",
                        "X-Auth-Token",
                        "X-Csrf-Token",
                        "X-Xsrf-Token"));
        configuration.setExposedHeaders(of("X-Csrf-Token", "X-Xsrf-Token"));
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
