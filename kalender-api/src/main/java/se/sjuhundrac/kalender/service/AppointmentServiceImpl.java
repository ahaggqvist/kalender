package se.sjuhundrac.kalender.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.repository.AppointmentRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {
    @NonNull
    private final StateService stateService;
    @NonNull
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentDetail> findAppointmentsByStartDateEndDate(AppointmentQuery query) {
        if (StringUtils.isBlank(query.getStart())
                || StringUtils.isBlank(query.getEnd())
                || StringUtils.isBlank(query.getFolderId())) {
            log.error("Invalid appointment query");
            return Collections.emptyList();
        }

        if (stateService.isAppointmentCacheEviction(query)) {
            appointmentRepository.evictCaches();
        }

        try {
            return appointmentRepository.findAppointments(query);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public List<AppointmentDetail> findAppointmentsForCurrentUser(AppointmentQuery query) {
        if (StringUtils.isBlank(query.getFolderId())) {
            log.error("Invalid appointment query");
            return Collections.emptyList();
        }

        try {
            return appointmentRepository.findAppointmentsByUsername(query);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public AppointmentDetail findAppointmentById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new NullPointerException("Appointment id is invalid");
        }

        try {
            return appointmentRepository.findAppointmentById(id);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public String saveOrUpdateAppointment(AppointmentDetail detail) {
        if (detail == null) {
            throw new NullPointerException("AppointmentDetail is null");
        }

        try {
            if (detail.isExistingAppointment()) {
                return appointmentRepository.updateAppointment(detail);
            }

            return appointmentRepository.saveNewAppointment(detail);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return "";
    }

    @Override
    public boolean deleteAppointment(AppointmentDetail detail) {
        try {
            return appointmentRepository.deleteAppointment(detail);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }
}
