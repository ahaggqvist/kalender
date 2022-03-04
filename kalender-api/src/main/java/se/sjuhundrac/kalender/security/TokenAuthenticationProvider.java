package se.sjuhundrac.kalender.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.util.AuthTokenUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static se.sjuhundrac.kalender.util.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @NonNull
    private final AppProperties appProperties;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        // Ignore
    }

    @Override
    protected UserDetails retrieveUser(
            String username, UsernamePasswordAuthenticationToken authentication) {

        if (StringUtils.isBlank(username) || (authentication.getCredentials() == null)) {
            throw new BadCredentialsException(ERROR_MESSAGE_BAD_TOKEN);
        }

        if (!StringUtils.isNumeric(authentication.getCredentials().toString())) {
            throw new BadCredentialsException(ERROR_MESSAGE_BAD_TOKEN);
        }

        // Timestamp.
        final var credentials = authentication.getCredentials().toString();
        final var timestamp = Long.parseLong(credentials);

        try {
            // Username is supplied token.
            if (AuthTokenUtil.isValidToken(username, appProperties.getApiKey(), timestamp)
                    && AuthTokenUtil.isValidTimestamp(timestamp)) {
                log.debug("Token {} and timestamp {}", username, timestamp);
                return User.withUsername(username).password(StringUtils.EMPTY).roles(DEFAULT_USER_ROLE).build();
            } else {
                log.error("{} {}", ERROR_MESSAGE_INVALID_TOKEN, username);
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | DecoderException e) {
            log.error(e.getMessage(), e);
        }

        throw new UsernameNotFoundException(ERROR_MESSAGE_INVALID_TOKEN);
    }
}
