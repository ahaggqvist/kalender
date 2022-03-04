package se.sjuhundrac.kalender;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class KalenderApiApplication implements ApplicationRunner {
    private final CacheManager cacheManager;

    public static void main(String[] args) {
        SpringApplication.run(KalenderApiApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        cacheManager.getCacheNames().forEach(cacheName -> log.debug("Evict cache {}", cacheName));
        cacheManager
                .getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }
}
