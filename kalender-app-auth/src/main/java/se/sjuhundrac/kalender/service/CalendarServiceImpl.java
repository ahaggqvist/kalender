package se.sjuhundrac.kalender.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.util.AuthTokenUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarServiceImpl implements CalendarService {
    private static final String FIND_ALL_CALENDARS_URI = "/calendars";
    @NonNull
    private final WebClient webClient;
    @NonNull
    private final AppProperties appProperties;

    @Override
    public Flux<CalendarDetail> findAllCalendars() {
        return webClient
                .get()
                .uri(FIND_ALL_CALENDARS_URI)
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .retrieve()
                .bodyToFlux(CalendarDetail.class);
    }
}
