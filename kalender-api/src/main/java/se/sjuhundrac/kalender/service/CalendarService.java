package se.sjuhundrac.kalender.service;

import microsoft.exchange.webservices.data.core.service.folder.Folder;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.CalendarQuery;

import java.util.List;

public interface CalendarService {
    List<CalendarDetail> findAllCalendars();

    Folder findFolder(CalendarQuery query);
}
