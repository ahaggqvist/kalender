package se.sjuhundrac.kalender.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(of = "apiUrl")
@ToString
@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    @NotBlank
    private String apiKey;
    @NotBlank
    private String apiUrl;
    @NotBlank
    private String userDisplayName;
    @NotBlank
    private String userUsername;
    @NotBlank
    private String userMail;
    @NotBlank
    private String userFirstname;
    @NotBlank
    private String userLastname;
    @NotBlank
    private String basicAuthUsername;
    @NotBlank
    private String basicAuthPassword;

}
