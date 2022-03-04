package se.sjuhundrac.kalender.service;

import reactor.core.publisher.Flux;
import se.sjuhundrac.kalender.model.Room;

public interface RoomService {

    Flux<Room> findRoomsByLocation(String location);
}
