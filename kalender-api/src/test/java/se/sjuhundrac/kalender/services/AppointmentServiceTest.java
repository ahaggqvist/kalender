package se.sjuhundrac.kalender.services;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.repository.AppointmentRepository;
import se.sjuhundrac.kalender.service.AppointmentServiceImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private static AppointmentQuery query;

    @BeforeAll
    static void init() {
        query =
                AppointmentQuery.builder()
                        .start("2020-05-11T12:30:00-02:00")
                        .end("2020-05-17T12:30:00-02:00")
                        .folderId(
                                "")
                        .build();
    }

    @Test
    void findAppointmentsByStartDateEndDateTest() throws Exception {
        when(appointmentRepository.findAppointments(query)).thenReturn(null);
        assertTrue(
                CollectionUtils.isEmpty(appointmentService.findAppointmentsByStartDateEndDate(query)));

        when(appointmentRepository.findAppointments(query))
                .thenReturn(Collections.singletonList(new AppointmentDetail()));
        assertTrue(
                CollectionUtils.isNotEmpty(appointmentService.findAppointmentsByStartDateEndDate(query)));

        when(appointmentRepository.findAppointments(query)).thenThrow(new Exception());
        assertTrue(
                CollectionUtils.isEmpty(appointmentService.findAppointmentsByStartDateEndDate(query)));

        assertTrue(
                CollectionUtils.isEmpty(
                        appointmentService.findAppointmentsByStartDateEndDate(
                                AppointmentQuery.builder()
                                        .start("")
                                        .end("2020-05-17T12:30:00-02:00")
                                        .folderId("abcde")
                                        .build())));
        assertTrue(
                CollectionUtils.isEmpty(
                        appointmentService.findAppointmentsByStartDateEndDate(
                                AppointmentQuery.builder()
                                        .start("2020-05-11T12:30:00-02:00")
                                        .end("")
                                        .folderId("")
                                        .build())));
        assertTrue(
                CollectionUtils.isEmpty(
                        appointmentService.findAppointmentsByStartDateEndDate(
                                AppointmentQuery.builder()
                                        .start("2020-05-11T12:30:00-02:00")
                                        .end("2020-05-17T12:30:00-02:00")
                                        .folderId("")
                                        .build())));
    }
}
