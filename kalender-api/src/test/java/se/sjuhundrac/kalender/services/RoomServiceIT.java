package se.sjuhundrac.kalender.services;

import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.service.RoomService;
import se.sjuhundrac.kalender.util.Constants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RoomServiceIT {
    @Autowired
    private RoomService roomService;

    @Test
    void findAllRoomsTest() {
        final EmailAddressCollection emailAddressColl = roomService.findAllRooms();
        assertTrue(CollectionUtils.isNotEmpty(emailAddressColl.getItems()));
    }

    @Test
    void findRoomsByMailTest() {
        assertTrue(
                CollectionUtils.isNotEmpty(
                        roomService.findRoomsByMail(Constants.LOCATIONS.get(""))));
    }
}
