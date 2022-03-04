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
import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

    public static HttpHeaders getApiHeaders(String apiKey) {
        final var timestamp =
                String.valueOf(DateTime.now(DateTimeZone.forID(Constants.EUROPE_BERLIN)).getMillis());
        var token = "";

        try {
            token = createToken(timestamp, apiKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | DecoderException e) {
            log.error(e.getMessage(), e);
        }

        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token is blank");
        }

        var headers = new HttpHeaders();
        headers.add(Constants.HEADER_TOKEN, token);
        headers.add(Constants.HEADER_TIMESTAMP, timestamp);
        return headers;
    }
}
