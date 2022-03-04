package se.sjuhundrac.kalender.repository;

import microsoft.exchange.webservices.data.core.service.item.Appointment;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;

import java.util.List;

public interface AppointmentRepository {
    List<AppointmentDetail> findAppointments(AppointmentQuery query) throws Exception;

    void evictCaches();

    List<AppointmentDetail> findAppointmentsByUsername(AppointmentQuery query) throws Exception;

    AppointmentDetail findAppointmentById(String id) throws Exception;

    String updateAppointment(AppointmentDetail appointmentDetail) throws Exception;

    String saveNewAppointment(AppointmentDetail appointmentDetail) throws Exception;

    boolean deleteAppointment(Appointment appointment) throws Exception;

    public boolean deleteAppointment(AppointmentDetail detail) throws Exception;
}
