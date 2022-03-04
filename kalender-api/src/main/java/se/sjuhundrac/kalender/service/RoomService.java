package se.sjuhundrac.kalender.service;

import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import se.sjuhundrac.kalender.model.Room;

import java.util.Collection;
import java.util.List;

public interface RoomService {
    List<Room> findRoomsByLocation(String location);

    Collection<EmailAddress> findRoomsByMail(String mail);

    EmailAddressCollection findAllRooms();
}
