package se.sjuhundrac.kalender.model;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode()
public class CalendarQuery {
    private final String folderId;
    private final String folderName;
}
