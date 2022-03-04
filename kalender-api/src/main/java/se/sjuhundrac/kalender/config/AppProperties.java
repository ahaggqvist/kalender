package se.sjuhundrac.kalender.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(of = "mailAddress")
@ToString
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppProperties {
    @NotBlank
    private String mailDisplayName;
    @NotBlank
    private String mailPassword;
    @NotBlank
    private String mailUrl;
    @NotBlank
    private String mailAddress;
    @NotBlank
    private String mailDomain;
    @NotBlank
    private String mailFolder;
    @NotBlank
    private String mailUsername;
    @NotBlank
    private String mailName;
    private String calendars;
    @NotBlank
    private String apiKey;
}
