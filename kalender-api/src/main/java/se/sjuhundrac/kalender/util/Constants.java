package se.sjuhundrac.kalender.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String HEADER_TOKEN = "token";
    public static final String HEADER_TIMESTAMP = "timestamp";
    public static final String DEFAULT_USER_ROLE = "USER";
    public static final String ERROR_MESSAGE_INVALID_TOKEN = "Access token is invalid";
    public static final String ERROR_MESSAGE_BAD_TOKEN = "Access token is bad";
    public static final String EUROPE_BERLIN = "Europe/Berlin";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PREFERRED_CULTURE = "sv-SE";
    public static final String LOCALE = "sv_SE";
    public static final int EXPIRE_IN_MINUTES = 2;
    public static final Map<String, String> LOCATIONS;

    static {
        LOCATIONS = Collections.emptyMap();
    }
}
