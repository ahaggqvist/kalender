package se.sjuhundrac.kalender.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.model.Room;
import se.sjuhundrac.kalender.util.AuthTokenUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    private static final String FIND_ROOMS_BY_LOCATION_URI = "/rooms?location={location}";
    @NonNull
    private final WebClient webClient;
    @NonNull
    private final AppProperties appProperties;

    @Override
    public Flux<Room> findRoomsByLocation(String location) {
        return webClient
                .get()
                .uri(FIND_ROOMS_BY_LOCATION_URI, location)
                .headers(
                        httpHeaders ->
                                httpHeaders.addAll(AuthTokenUtil.getApiHeaders(appProperties.getApiKey())))
                .retrieve()
                .bodyToFlux(Room.class);
    }
}
