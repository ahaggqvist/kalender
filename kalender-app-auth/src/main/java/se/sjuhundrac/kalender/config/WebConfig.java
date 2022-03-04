package se.sjuhundrac.kalender.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        viewControllerRegistry.addViewController("/**/{path:[^.]*}").setViewName("forward:/");
        viewControllerRegistry.addViewController("/logout-success").setViewName("logout-success");
    }

    @Bean
    TomcatContextCustomizer sameSiteCookiesConfig() {
        return context -> {
            final var rfc6265CookieProcessor = new Rfc6265CookieProcessor();
            rfc6265CookieProcessor.setSameSiteCookies(SameSiteCookies.STRICT.getValue());
            context.setCookieProcessor(rfc6265CookieProcessor);
        };
    }
}
