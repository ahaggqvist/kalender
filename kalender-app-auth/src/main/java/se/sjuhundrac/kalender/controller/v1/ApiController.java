package se.sjuhundrac.kalender.controller.v1;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.Room;
import se.sjuhundrac.kalender.security.UserInfo;
import se.sjuhundrac.kalender.security.UserInfoHolder;
import se.sjuhundrac.kalender.service.AppointmentService;
import se.sjuhundrac.kalender.service.CalendarService;
import se.sjuhundrac.kalender.service.RoomService;

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
    @NonNull
    private final UserInfoHolder userInfoHolder;

    @GetMapping(path = "/account")
    public ResponseEntity<UserInfo> account() {
        return new ResponseEntity<>(userInfoHolder.getUserInfo(), HttpStatus.OK);
    }

    @GetMapping(path = "/rooms")
    public ResponseEntity<Flux<Room>> rooms(String location) {
        return new ResponseEntity<>(
                roomService
                        .findRoomsByLocation(location)
                        .log()
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                HttpStatus.OK);
    }

    @GetMapping(path = "/calendars")
    public ResponseEntity<Flux<CalendarDetail>> findCalendars() {
        return new ResponseEntity<>(
                calendarService
                        .findAllCalendars()
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                HttpStatus.OK);
    }

    @GetMapping(path = "/appointments-folderid")
    public ResponseEntity<Mono<List<AppointmentDetail>>> findAllAppointments(AppointmentQuery query) {
        return new ResponseEntity<>(
                appointmentService
                        .findAppointmentsByStartDateEndDate(query)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                HttpStatus.OK);
    }

    @GetMapping(path = "/appointments-attendee-folderid")
    public ResponseEntity<Mono<List<AppointmentDetail>>> findAppointmentsByAttendeeFolderId(
            AppointmentQuery query) {
        return new ResponseEntity<>(
                appointmentService
                        .findAppointmentsByUser(query)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                HttpStatus.OK);
    }

    @GetMapping(path = "/appointment")
    public ResponseEntity<Mono<AppointmentDetail>> findAppointmentById(@RequestParam String id) {
        return new ResponseEntity<>(
                appointmentService
                        .findAppointmentById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                HttpStatus.OK);
    }

    @PostMapping(path = "/appointment")
    public ResponseEntity<Mono<Void>> saveOrUpdateAppointment(
            @RequestBody AppointmentDetail detail) {
        return new ResponseEntity<>(
                appointmentService.saveOrUpdateAppointment(detail), HttpStatus.OK);
    }

    @PostMapping(path = "/delete-appointment")
    public ResponseEntity<Mono<Void>> deleteAppointment(
            @RequestBody AppointmentDetail detail) {
        return new ResponseEntity<>(
                appointmentService.deleteAppointment(detail), HttpStatus.OK);
    }
}
