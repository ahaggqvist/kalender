package se.sjuhundrac.kalender.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.CalendarQuery;
import se.sjuhundrac.kalender.repository.CalendarRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {
    private final AppProperties appProperties;
    private final CalendarRepository calendarRepository;

    public CalendarServiceImpl(@NonNull CalendarRepository calendarRepository, @NonNull AppProperties appProperties) {
        this.calendarRepository = calendarRepository;
        this.appProperties = appProperties;
    }

    @Override
    @Cacheable(value = "calendarsCache", key = "#root.methodName")
    public List<CalendarDetail> findAllCalendars() {
        try {
            var calendarFolderIds = Arrays.asList(StringUtils.split(appProperties.getCalendars(), ";"));
            if (CollectionUtils.isNotEmpty(calendarFolderIds)) {
                return calendarRepository.findAllCalendars().stream().filter(c -> calendarFolderIds.contains(c.getId())).toList();
            }

            return calendarRepository.findAllCalendars();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public Folder findFolder(CalendarQuery query) {
        try {
            final var folder = calendarRepository.findFolder(query);
            if (folder == null) {
                throw new NullPointerException(
                        "Folder with Id "
                                + query.getFolderId()
                                + " and name "
                                + query.getFolderName()
                                + " was not found");
            }

            return folder;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
