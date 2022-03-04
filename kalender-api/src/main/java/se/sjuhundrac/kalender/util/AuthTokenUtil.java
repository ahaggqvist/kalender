package se.sjuhundrac.kalender.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static se.sjuhundrac.kalender.util.Constants.EUROPE_BERLIN;
import static se.sjuhundrac.kalender.util.Constants.EXPIRE_IN_MINUTES;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthTokenUtil {

    public static String createToken(String s, String apiKey)
            throws InvalidKeyException, NoSuchAlgorithmException, DecoderException {
        final var mac = Mac.getInstance(HmacAlgorithms.HMAC_SHA_256.toString());
        final var hex = Hex.decodeHex(apiKey);

        final var secretKey = new SecretKeySpec(hex, mac.getAlgorithm());
        mac.init(secretKey);

        return new String(Base64.encodeBase64(mac.doFinal(s.getBytes(StandardCharsets.UTF_8))));
    }

    public static boolean isValidToken(String token, String apiKey, long timestamp)
            throws InvalidKeyException, NoSuchAlgorithmException, DecoderException {
        return StringUtils.equals(token, createToken(String.valueOf(timestamp), apiKey));
    }

    public static boolean isValidTimestamp(long timestamp) {
        final var minutes =
                Minutes.minutesBetween(
                                new DateTime(timestamp).withZone(DateTimeZone.forID(EUROPE_BERLIN)),
                                DateTime.now(DateTimeZone.forID(EUROPE_BERLIN)))
                        .getMinutes();
        log.debug("Minutes between {}", minutes);
        return (minutes >= 0) && (minutes <= EXPIRE_IN_MINUTES);
    }
}
