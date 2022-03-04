package se.sjuhundrac.kalender.security;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import se.sjuhundrac.kalender.config.AppProperties;

@AllArgsConstructor
@Component
public class UserInfoHolder {
    private final AppProperties appProperties;
    private final Environment environment;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserInfo getUserInfo() {
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            return UserInfo.create(
                    appProperties.getUserDisplayName(), appProperties.getUserUsername(), appProperties.getUserMail(), appProperties.getUserFirstname(), appProperties.getUserLastname(), null);
        }

        return (UserInfo) this.getAuthentication().getPrincipal();
    }
}
