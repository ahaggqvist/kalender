package se.sjuhundrac.kalender.services;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.service.AppointmentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppointmentServiceIT {
    @Autowired
    private AppointmentService appointmentService;

    @Test
    void findAppointmentsByStartDateEndDateTest() {
        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .start("2020-05-11T12:30:00-02:00")
                        .end("2020-05-17T12:30:00-02:00")
                        .folderId(
                                "")
                        .build();

        assertTrue(
                CollectionUtils.isNotEmpty(appointmentService.findAppointmentsByStartDateEndDate(query)));
    }

    @Test
    void findAppointmentByIdTest() {
        final String id =
                "";
        final AppointmentDetail detail = appointmentService.findAppointmentById(id);
        assertNotNull(detail);
    }

    @Test
    void findAppointmentsByAttendeeTest() {
        final AppointmentQuery query =
                AppointmentQuery.builder().folderName("").build();
        final List<AppointmentDetail> details =
                appointmentService.findAppointmentsForCurrentUser(query);

        assertTrue(CollectionUtils.isNotEmpty(details));
    }

    @Test
    void saveAppointmentTest() {
        final AppointmentDetail detail = new AppointmentDetail();
        detail.setStartDate(
                DateTime.now().plusDays(1).withHourOfDay(8).withMinuteOfHour(0).toDate());
        detail.setEndDate(
                DateTime.now().plusDays(1).withHourOfDay(12).withMinuteOfHour(0).toDate());
        detail.setSubject("");
        detail.setFolderId(
                "");
        detail.setFolderName("");

        final String id = appointmentService.saveOrUpdateAppointment(detail);
        final AppointmentDetail appointment = appointmentService.findAppointmentById(id);
        assertNotNull(appointment);
    }
}
