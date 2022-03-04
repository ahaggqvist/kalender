package se.sjuhundrac.kalender.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String EUROPE_BERLIN = "Europe/Berlin";
    public static final String HEADER_TOKEN = "token";
    public static final String HEADER_TIMESTAMP = "timestamp";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LOCALE = "sv_SE";
    public static final Map<String, String> LOCATIONS;

    static {
        LOCATIONS = Collections.emptyMap();
    }
}
