package se.sjuhundrac.kalender.util;

import org.springframework.cache.interceptor.SimpleKeyGenerator;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.repository.AppointmentRepository;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CustomKeyGenerator extends SimpleKeyGenerator {

    @Override
    @Nonnull
    public Object generate(
            @Nonnull Object target, @Nonnull Method method, @Nonnull Object... params) {
        if (target instanceof AppointmentRepository) {
            try {
                if (Arrays.stream(target.getClass().getDeclaredMethods())
                        .anyMatch(m -> m.isAnnotationPresent(CustomGenerator.class))) {
                    for (final Object obj : params) {
                        if (obj instanceof AppointmentQuery query) {
                            return CacheUtil.createAppointmentCacheKey(query);
                        }
                    }
                }
            } catch (final SecurityException e) {
                // Ignore
            }
        }

        return super.generate(target, method, params);
    }
}
