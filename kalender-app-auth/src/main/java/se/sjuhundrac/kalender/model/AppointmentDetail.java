package se.sjuhundrac.kalender.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import static se.sjuhundrac.kalender.util.Constants.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetail implements Comparable<AppointmentDetail> {
    private String id;

    @JsonProperty("start")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            locale = LOCALE,
            timezone = EUROPE_BERLIN,
            pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date startDate;

    @JsonProperty("end")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            locale = LOCALE,
            timezone = EUROPE_BERLIN,
            pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date endDate;

    @JsonProperty("title")
    private String subject;

    private String backgroundColor;
    private String folderName;
    private String folderId;
    private String username;
    private ExtendedProps extendedProps = new ExtendedProps();

    @Override
    public int compareTo(@Nonnull AppointmentDetail otherAppointment) {
        return Comparator.comparing(AppointmentDetail::getStartDate)
                .thenComparing(AppointmentDetail::getEndDate)
                .thenComparing(AppointmentDetail::getSubject)
                .compare(this, otherAppointment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endDate, startDate);
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
        final var other = (AppointmentDetail) obj;
        if (!Objects.equals(endDate, other.endDate)) {
            return false;
        }
        return Objects.equals(startDate, other.startDate);
    }
}
