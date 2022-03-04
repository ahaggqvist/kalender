package se.sjuhundrac.kalender.services;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.repository.AppointmentRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppointmentRepositoryIT {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Test
    void findAllAppointmentsTest() throws Exception {
        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .start("2020-05-11T12:30:00-02:00")
                        .end("2020-05-17T12:30:00-02:00")
                        .folderId(
                                "")
                        .build();

        assertTrue(CollectionUtils.isNotEmpty(appointmentRepository.findAppointments(query)));
    }
}
