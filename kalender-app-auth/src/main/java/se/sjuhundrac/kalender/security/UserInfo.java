package se.sjuhundrac.kalender.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(of = "username")
public class UserInfo implements UserDetails {
    @JsonIgnore
    private Long id;
    private String displayName;
    private String username;
    @JsonIgnore
    private String password;
    private String mail;
    private String firstName;
    private String lastName;
    @JsonIgnore
    private boolean isAccountNonExpired;
    @JsonIgnore
    private boolean isAccountNonLocked;
    @JsonIgnore
    private boolean isCredentialsNonExpired;
    @JsonIgnore
    private boolean isEnabled;
    @JsonIgnore
    private Set<AuthorityInfo> authorities;

    private UserInfo(
            String displayName,
            String username,
            String mail,
            String firstName,
            String lastName,
            Set<AuthorityInfo> authorities) {
        this.displayName = displayName;
        this.username = username;
        this.mail = mail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    public static UserInfo create(
            String displayName,
            String username,
            String mail,
            String firstName,
            String lastName,
            Set<AuthorityInfo> authorities) {
        return new UserInfo(displayName, username, mail, firstName, lastName, authorities);
    }
}
