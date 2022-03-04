package se.sjuhundrac.kalender.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.service.AppointmentService;
import se.sjuhundrac.kalender.service.StateService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class StateServiceIT {
    @Autowired
    AppointmentService appointmentService;
    @Autowired
    StateService stateService;

    @BeforeAll
    void init() {
        final AppointmentDetail appointmentDetail = new AppointmentDetail();
        appointmentDetail.setStartDate(
                DateTime.now().plusDays(1).withHourOfDay(8).withMinuteOfHour(0).toDate());
        appointmentDetail.setEndDate(
                DateTime.now().plusDays(1).withHourOfDay(12).withMinuteOfHour(0).toDate());
        appointmentDetail.setFolderId(
                "");
        appointmentDetail.setFolderName("");

        final String id = appointmentService.saveOrUpdateAppointment(appointmentDetail);
        assertNotNull(id);

        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .folderId(
                                "")
                        .start("2021-01-09T08:00:00-02:00")
                        .end("2021-01-09T17:00:00-02:00")
                        .build();

        List<AppointmentDetail> details =
                appointmentService.findAppointmentsByStartDateEndDate(query);

        assertTrue(CollectionUtils.isNotEmpty(details));
    }

    @Test
    void isAppointmentRefreshTest() {
        final String folderId =
                "";
        final AppointmentQuery query =
                AppointmentQuery.builder()
                        .folderId(folderId)
                        .start("2021-01-09T08:00:00-02:00")
                        .end("2021-01-09T17:00:00-02:00")
                        .build();

        assertFalse(stateService.isAppointmentCacheEviction(query));

        final AppointmentDetail detail = new AppointmentDetail();
        detail.setStartDate(
                DateTime.now().plusDays(1).withHourOfDay(12).withMinuteOfHour(0).toDate());
        detail.setEndDate(
                DateTime.now().plusDays(1).withHourOfDay(17).withMinuteOfHour(0).toDate());
        detail.setFolderId(
                "");
        detail.setFolderName("");

        final String id = appointmentService.saveOrUpdateAppointment(detail);
        assertNotNull(id);

        List<AppointmentDetail> details =
                appointmentService.findAppointmentsByStartDateEndDate(query);

        assertTrue(CollectionUtils.isNotEmpty(details));
        assertTrue(stateService.isAppointmentCacheEviction(query));
    }
}
