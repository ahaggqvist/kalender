package se.sjuhundrac.kalender.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = -4029611734544145585L;
    private String displayName;
    private String mail;

    @Override
    public int hashCode() {
        return Objects.hash(mail);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final var other = (AttendeeDetail) obj;
        return !Objects.equals(mail, other.mail);
    }
}
