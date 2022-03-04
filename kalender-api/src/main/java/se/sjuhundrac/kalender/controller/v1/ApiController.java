package se.sjuhundrac.kalender.controller.v1;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.Room;
import se.sjuhundrac.kalender.service.AppointmentService;
import se.sjuhundrac.kalender.service.CalendarService;
import se.sjuhundrac.kalender.service.RoomService;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiController {
    @NonNull
    private final AppointmentService appointmentService;
    @NonNull
    private final CalendarService calendarService;
    @NonNull
    private final RoomService roomService;

    @GetMapping(path = "/rooms")
    public List<Room> rooms(String location) {
        return roomService.findRoomsByLocation(location);
    }

    @GetMapping(path = "/calendars")
    public List<CalendarDetail> findCalendars() {
        return calendarService.findAllCalendars();
    }

    @GetMapping(path = "/appointments-folderid")
    public List<AppointmentDetail> findAllAppointments(AppointmentQuery query) {
        return appointmentService.findAppointmentsByStartDateEndDate(query).stream()
                .sorted(Comparator.comparing(AppointmentDetail::getStartDate))
                .toList();
    }

    @GetMapping(path = "/appointments-attendee-folderid")
    public List<AppointmentDetail> findAppointmentsByAttendeeFolderId(AppointmentQuery query) {
        return appointmentService.findAppointmentsForCurrentUser(query).stream()
                .sorted(Comparator.comparing(AppointmentDetail::getStartDate))
                .toList();
    }

    @GetMapping(path = "/appointment")
    public AppointmentDetail findAppointmentById(@RequestParam String id) {
        return appointmentService.findAppointmentById(id);
    }

    @PostMapping(path = "/appointment")
    public String saveOrUpdateAppointment(@RequestBody AppointmentDetail detail) {
        return appointmentService.saveOrUpdateAppointment(detail);
    }

    @PostMapping(path = "/delete-appointment")
    public boolean deleteAppointment(@RequestBody AppointmentDetail detail) {
        return appointmentService.deleteAppointment(detail);
    }
}
