package se.sjuhundrac.kalender.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import se.sjuhundrac.kalender.model.AppointmentQuery;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheUtil {

    public static String createAppointmentCacheKey(AppointmentQuery query) {
        final var sb = new StringBuilder();
        if (StringUtils.isNotBlank(query.getFolderName())) {
            sb.append(query.getFolderName().replaceAll("\\s", "").toLowerCase());
        } else if (StringUtils.isNotBlank(query.getFolderId())) {
            sb.append(query.getFolderId());
        }

        sb.append(query.getStart()).append(query.getEnd());
        return sb.toString();
    }
}
