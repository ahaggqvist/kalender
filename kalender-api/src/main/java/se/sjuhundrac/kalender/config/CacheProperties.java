package se.sjuhundrac.kalender.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@EqualsAndHashCode(of = "redisPort")
@ToString
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    @NotBlank
    private String redisHostname = "localhost";
    @NotBlank
    private String redisPassword;
    private int redisPort = 6379;
    private int redisDataTtl = 1;
}
