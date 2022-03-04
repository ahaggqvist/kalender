package se.sjuhundrac.kalender.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import se.sjuhundrac.kalender.util.CustomKeyGenerator;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Configuration
@EnableCaching
class CacheConfig extends CachingConfigurerSupport {
    private CacheProperties cacheProperties;

    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        var redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(
                        cacheProperties.getRedisHostname(), cacheProperties.getRedisPort());
        redisStandaloneConfiguration.setPassword(cacheProperties.getRedisPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        var redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        var redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .disableCachingNullValues()
                        .entryTtl(Duration.ofHours(cacheProperties.getRedisDataTtl()))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));

        redisCacheConfiguration.usePrefix();

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(
                        lettuceConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .initialCacheNames(
                        new HashSet<>(
                                Arrays.asList(
                                        "calendarsCache",
                                        "calendarFolderIdCache",
                                        "calendarFolderNameCache",
                                        "appointmentsAttendeeCache",
                                        "appointmentsCache",
                                        "appointmentsAttendeeCache")))
                .build();
    }

    @Override
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}
