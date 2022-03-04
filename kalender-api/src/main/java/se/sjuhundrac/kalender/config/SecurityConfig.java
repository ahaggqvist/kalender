package se.sjuhundrac.kalender.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.sjuhundrac.kalender.security.TokenAuthenticationProvider;
import se.sjuhundrac.kalender.security.TokenFilter;

import java.util.Collections;

import static se.sjuhundrac.kalender.util.Constants.HEADER_TIMESTAMP;
import static se.sjuhundrac.kalender.util.Constants.HEADER_TOKEN;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
    @NonNull
    private final TokenAuthenticationProvider tokenAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authenticationProvider(tokenAuthenticationProvider)
                .addFilterBefore(tokenFilter(), AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/**")
                .authenticated()
                .antMatchers("/")
                .permitAll()
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .logout()
                .disable();
    }

    @Bean
    TokenFilter tokenFilter() throws Exception {
        final var tokenFilter = new TokenFilter(new AntPathRequestMatcher("/api/**"));
        tokenFilter.setAuthenticationManager(authenticationManager());
        tokenFilter.setAuthenticationSuccessHandler(successHandler());
        return tokenFilter;
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final var simpleUrlAuthenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
        simpleUrlAuthenticationSuccessHandler.setRedirectStrategy((request, response, url) -> {
        });
        return simpleUrlAuthenticationSuccessHandler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
                java.util.List.of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(
                java.util.List.of(
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "Content-Type",
                        HEADER_TOKEN,
                        HEADER_TIMESTAMP));
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
