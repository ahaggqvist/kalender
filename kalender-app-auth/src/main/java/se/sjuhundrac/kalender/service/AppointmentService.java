package se.sjuhundrac.kalender.service;

import reactor.core.publisher.Mono;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;

import java.util.List;

public interface AppointmentService {

    Mono<List<AppointmentDetail>> findAppointmentsByStartDateEndDate(AppointmentQuery query);

    Mono<List<AppointmentDetail>> findAppointmentsByUser(AppointmentQuery query);

    Mono<AppointmentDetail> findAppointmentById(String id);

    Mono<Void> saveOrUpdateAppointment(AppointmentDetail detail);

    Mono<Void> deleteAppointment(AppointmentDetail detail);
}
