package se.sjuhundrac.kalender.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;

import java.util.Locale;

import static se.sjuhundrac.kalender.util.Constants.PREFERRED_CULTURE;

@Builder
@Getter
@AllArgsConstructor
public class ExchangeProperties {
    private final String username;
    private final String password;
    private final String domain;
    private final String url;
    @Builder.Default
    private final Locale preferredCulture = Locale.forLanguageTag(PREFERRED_CULTURE);
    @Builder.Default
    private final ExchangeVersion exchangeVersion = ExchangeVersion.Exchange2010_SP2;
}
