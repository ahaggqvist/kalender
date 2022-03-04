package se.sjuhundrac.kalender.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@Configuration
class ExchangeConfig {
    @NonNull
    private final AppProperties appProperties;

    @Bean(destroyMethod = "close")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ExchangeService exchangeService() throws URISyntaxException {
        var exchangeProperties =
                ExchangeProperties.builder()
                        .domain(appProperties.getMailDomain())
                        .url(appProperties.getMailUrl())
                        .username(appProperties.getMailUsername())
                        .password(appProperties.getMailPassword())
                        .build();

        var webCredentials =
                new WebCredentials(
                        exchangeProperties.getUsername(), exchangeProperties.getPassword(), exchangeProperties.getDomain());

        var service = new ExchangeService(exchangeProperties.getExchangeVersion());
        service.setUrl(new URI(exchangeProperties.getUrl()));
        service.setCredentials(webCredentials);
        service.setPreferredCulture(exchangeProperties.getPreferredCulture());
        return service;
    }
}
