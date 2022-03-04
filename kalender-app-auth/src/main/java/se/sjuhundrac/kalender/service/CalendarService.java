package se.sjuhundrac.kalender.service;

import reactor.core.publisher.Flux;
import se.sjuhundrac.kalender.model.CalendarDetail;

public interface CalendarService {

    Flux<CalendarDetail> findAllCalendars();
}
