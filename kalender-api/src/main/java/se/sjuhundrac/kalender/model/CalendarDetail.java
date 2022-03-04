package se.sjuhundrac.kalender.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 2720361930845561433L;
    private String id;
    private String folderName;
}
