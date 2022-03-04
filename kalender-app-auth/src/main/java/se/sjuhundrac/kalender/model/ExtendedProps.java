package se.sjuhundrac.kalender.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static se.sjuhundrac.kalender.util.Constants.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtendedProps {
    private String createdBy;
    private String mail;
    private String displayName;
    private String iCalUid;
    private String location;
    private String lastModifiedName;
    private String body;

    @JsonFormat(locale = LOCALE, timezone = EUROPE_BERLIN, pattern = YYYY_MM_DD_HH_MM)
    private Date dateTimeCreated;

    private String organizerMail;
    private String organizerName;
    private List<AttendeeDetail> attendees = new ArrayList<>();
}
