package se.sjuhundrac.kalender.repository;

import microsoft.exchange.webservices.data.core.service.folder.Folder;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.CalendarQuery;

import java.util.List;

public interface CalendarRepository {
    List<CalendarDetail> findAllCalendars() throws Exception;

    Folder findFolder(CalendarQuery query) throws Exception;

    Folder findCalendarFolderById(CalendarQuery query) throws Exception;

    Folder findCalendarFolderByName(CalendarQuery query) throws Exception;
}
