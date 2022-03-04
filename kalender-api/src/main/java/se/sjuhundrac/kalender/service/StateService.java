package se.sjuhundrac.kalender.service;

import se.sjuhundrac.kalender.model.AppointmentQuery;

public interface StateService {
    boolean isAppointmentCacheEviction(AppointmentQuery query);
}
