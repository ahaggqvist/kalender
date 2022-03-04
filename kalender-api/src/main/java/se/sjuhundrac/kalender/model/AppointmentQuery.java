package se.sjuhundrac.kalender.model;

import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import static se.sjuhundrac.kalender.util.Constants.EUROPE_BERLIN;

@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode()
public class AppointmentQuery {
    private static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.forID(EUROPE_BERLIN);
    private final String folderId;
    private final String folderName;
    private final String attendee;
    private final String id;
    private final String start;
    private final String end;
    private final String timeZone;
    private final String username;

    public DateTime getStartAsDateTime() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(start).withZone(DATE_TIME_ZONE);
    }

    public DateTime getEndAsDateTime() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(end).withZone(DATE_TIME_ZONE);
    }
}
