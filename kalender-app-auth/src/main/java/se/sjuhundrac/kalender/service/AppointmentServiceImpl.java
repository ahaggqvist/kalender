package se.sjuhundrac.kalender.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.model.AppointmentDetail;
import se.sjuhundrac.kalender.model.AppointmentQuery;
import se.sjuhundrac.kalender.security.UserInfoHolder;
import se.sjuhundrac.kalender.util.AuthTokenUtil;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {
    private static final String APPOINTMENTS_BY_START_END_URI =
            "/appointments-folderid?folderId={folderId}&start={start}&end={end}";
    private static final String APPOINTMENTS_BY_USER_URI =
            "/appointments-attendee-folderid?folderId={folderId}&username={username}";
    private static final String APPOINTMENT_BY_ID_URI = "/appointment?id={id}";
    private static final String SAVE_UPDATE_APPOINTMENT_URI = "/appointment";
    private static final String DELETE_APPOINTMENT_URI = "/delete-appointment";

    @NonNull
    private final WebClient webClient;
    @NonNull
    private final UserInfoHolder userInfoHolder;
    @NonNull
    private final AppProperties appProperties;

    @Override
    public Mono<List<AppointmentDetail>> findAppointmentsByStartDateEndDate(AppointmentQuery query) {
        return webClient
                .get()
                .uri(APPOINTMENTS_BY_START_END_URI, query.getFolderId(), query.getStart(), query.getEnd())
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .retrieve()
                .bodyToFlux(AppointmentDetail.class)
                .log()
                .timeout(Duration.ofSeconds(10))
                .collectSortedList(Comparator.comparing(AppointmentDetail::getStartDate));
    }

    @Override
    public Mono<List<AppointmentDetail>> findAppointmentsByUser(AppointmentQuery query) {
        return webClient
                .get()
                .uri(
                        APPOINTMENTS_BY_USER_URI,
                        query.getFolderId(),
                        userInfoHolder.getUserInfo().getUsername())
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .retrieve()
                .bodyToFlux(AppointmentDetail.class)
                .collectSortedList(Comparator.comparing(AppointmentDetail::getStartDate));
    }

    @Override
    public Mono<AppointmentDetail> findAppointmentById(String id) {
        return webClient
                .get()
                .uri(APPOINTMENT_BY_ID_URI, id)
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .retrieve()
                .bodyToMono(AppointmentDetail.class);
    }

    @Override
    public Mono<Void> saveOrUpdateAppointment(AppointmentDetail detail) {
        return webClient
                .post()
                .uri(SAVE_UPDATE_APPOINTMENT_URI)
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .body(Mono.just(verifyAndMapDetail(detail)), AppointmentDetail.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> deleteAppointment(AppointmentDetail detail) {
        verifyAndMapDetail(detail);
        return webClient
                .post()
                .uri(DELETE_APPOINTMENT_URI)
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .body(Mono.just(verifyAndMapDetail(detail)), AppointmentDetail.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private AppointmentDetail verifyAndMapDetail(AppointmentDetail detail) {
        if (detail == null) {
            throw new NullPointerException("Detail is null");
        }

        var userInfo = userInfoHolder.getUserInfo();
        if (userInfo == null) {
            throw new NullPointerException("UserInfo is null");
        }

        detail.setUsername(userInfo.getUsername());
        detail.getExtendedProps().setDisplayName(userInfo.getDisplayName());
        detail.getExtendedProps().setMail(userInfo.getMail());
        detail.getExtendedProps().setCreatedBy(userInfo.getUsername());

        return detail;
    }
}
