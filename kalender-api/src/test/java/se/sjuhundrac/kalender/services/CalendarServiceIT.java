package se.sjuhundrac.kalender.services;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sjuhundrac.kalender.service.CalendarService;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CalendarServiceIT {
    @Autowired
    private CalendarService calendarService;

    @Test
    void findCalendarsTest() {
        assertTrue(CollectionUtils.isNotEmpty(calendarService.findAllCalendars()));
    }
}
