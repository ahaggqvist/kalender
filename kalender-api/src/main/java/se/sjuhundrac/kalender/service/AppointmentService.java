package se.sjuhundrac.kalender.service;

import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;

import java.util.List;

public interface AppointmentService {

    List<AppointmentDetail> findAppointmentsByStartDateEndDate(AppointmentQuery query);

    List<AppointmentDetail> findAppointmentsForCurrentUser(AppointmentQuery query);

    AppointmentDetail findAppointmentById(String id);

    String saveOrUpdateAppointment(AppointmentDetail detail);

    boolean deleteAppointment(AppointmentDetail detail);
}
