package se.sjuhundrac.kalender.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static se.sjuhundrac.kalender.util.Constants.*;

public final class TokenFilter extends AbstractAuthenticationProcessingFilter {

    public TokenFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) {
        final var token = request.getHeader(HEADER_TOKEN);
        final var timestamp = request.getHeader(HEADER_TIMESTAMP);

        if (StringUtils.isBlank(token) || StringUtils.isBlank(timestamp)) {
            throw new BadCredentialsException(ERROR_MESSAGE_BAD_TOKEN);
        }

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(token, timestamp));
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
