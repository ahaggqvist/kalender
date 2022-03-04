package se.sjuhundrac.kalender.model;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode()
public class AppointmentQuery {
    private final String folderId;
    private final String folderName;
    private final String attendee;
    private final String id;
    private final String start;
    private final String end;
    private final String timeZone;
    private final String username;
}
