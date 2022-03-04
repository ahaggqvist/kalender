package se.sjuhundrac.kalender.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.model.Room;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static se.sjuhundrac.kalender.util.Constants.LOCATIONS;

@Slf4j
@AllArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    @NonNull
    private final ObjectProvider<ExchangeService> exchangeServiceProvider;

    @Override
    public List<Room> findRoomsByLocation(String location) {
        if (!LOCATIONS.containsKey(location)) {
            log.error("Location {} is unknown", location);
            return Collections.emptyList();
        }

        return findRoomsByMail(LOCATIONS.get(location)).stream()
                .map(e -> new Room(e.getAddress(), e.getName()))
                .toList();
    }

    @Override
    public Collection<EmailAddress> findRoomsByMail(String mail) {
        try (final var service = exchangeServiceProvider.getObject()) {
            return service.getRooms(new EmailAddress(mail));
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public EmailAddressCollection findAllRooms() {
        try (final var service = exchangeServiceProvider.getObject()) {
            return service.getRoomLists();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
