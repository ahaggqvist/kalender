package se.sjuhundrac.kalender.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.model.AppointmentQuery;

import java.util.Objects;

@Slf4j
@SpringBootTest
class AppointmentServiceTest {
    @Autowired
    AppointmentService appointmentService;

    @Test
    void findAppointmentsByStartDateEndDateTest() {
        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .start("2021-02-08T12:30:00-01:00")
                        .end("2021-02-14T12:30:00-01:00")
                        .folderId(
                                "")
                        .build();

        var appointments = appointmentService.findAppointmentsByStartDateEndDate(query);
        Objects.requireNonNull(appointments.block()).forEach(a -> log.debug(a.toString()));
    }

    @Test
    void findAppointmentsForCurrentUserTest() {
        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .username("")
                        .folderId(
                                "")
                        .build();

        var appointments = appointmentService.findAppointmentsByUser(query);
        Objects.requireNonNull(appointments.block()).forEach(a -> log.debug(a.toString()));
    }
}
