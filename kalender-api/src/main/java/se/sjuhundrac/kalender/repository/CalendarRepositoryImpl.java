package se.sjuhundrac.kalender.repository;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.config.AppProperties;
import se.sjuhundrac.kalender.model.CalendarDetail;
import se.sjuhundrac.kalender.model.CalendarQuery;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CalendarRepositoryImpl implements CalendarRepository {
    private static final int MIN_PAGESIZE = 1;
    private static final int MAX_PAGESIZE = 100;

    private final ObjectProvider<ExchangeService> exchangeServiceProvider;
    private final AppProperties appProperties;

    public CalendarRepositoryImpl(
            @NonNull ObjectProvider<ExchangeService> exchangeServiceProvider,
            @NonNull AppProperties appProperties) {
        this.exchangeServiceProvider = exchangeServiceProvider;
        this.appProperties = appProperties;
    }

    @Override
    public List<CalendarDetail> findAllCalendars() throws Exception {
        return findAllCalendarFolders().stream()
                .map(f -> new CalendarDetail(f.getId().getUniqueId(), getFolderDisplayName(f)))
                .toList();
    }

    private String getFolderDisplayName(Folder folder) {
        try {
            return folder.getDisplayName();
        } catch (final ServiceLocalException e) {
            log.error(e.getMessage(), e);
        }

        return "";
    }

    @Override
    public Folder findFolder(CalendarQuery query) throws Exception {
        if (StringUtils.isNotBlank(query.getFolderId())
                && !StringUtils.equalsIgnoreCase(query.getFolderId(), "undefined")) {
            return findCalendarFolderById(query);
        }

        if (StringUtils.isNotBlank(query.getFolderName())) {
            return findCalendarFolderByName(query);
        }

        return null;
    }

    @Cacheable(
            value = "calendarFolderIdCache",
            key = "#root.methodName + #query.folderId",
            condition = "#query.folderId != null",
            unless = "#result==null")
    public Folder findCalendarFolderById(CalendarQuery query) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            return Folder.bind(service, FolderId.getFolderIdFromString(query.getFolderId()));
        }
    }

    @Cacheable(
            value = "calendarFolderNameCache",
            key = "#root.methodName + #query.folderName",
            condition = "#query.folderName != null",
            unless = "#result==null")
    public Folder findCalendarFolderByName(CalendarQuery query) throws Exception {
        final var view = new FolderView(MIN_PAGESIZE);
        final SearchFilter searchFilter =
                new SearchFilter.IsEqualTo(FolderSchema.DisplayName, query.getFolderName());

        final var mailbox = Mailbox.getMailboxFromString(appProperties.getMailAddress());
        final var folderId = new FolderId(WellKnownFolderName.Calendar, mailbox);

        try (final var service = exchangeServiceProvider.getObject()) {
            final var findFolderResults = service.findFolders(folderId, searchFilter, view);

            if ((findFolderResults != null)
                    && CollectionUtils.isNotEmpty(findFolderResults.getFolders())) {
                log.debug("Find folders total count {}", findFolderResults.getTotalCount());

                return findFolderResults.getFolders().get(0);
            }
        }

        return null;
    }

    private List<Folder> findAllCalendarFolders() throws Exception {
        final var view = new FolderView(MAX_PAGESIZE);
        final SearchFilter searchFilter =
                new SearchFilter.IsEqualTo(FolderSchema.FolderClass, "IPF.Appointment");

        final var mailbox = Mailbox.getMailboxFromString(appProperties.getMailAddress());
        final var folderId = new FolderId(WellKnownFolderName.Calendar, mailbox);

        try (final var service = exchangeServiceProvider.getObject()) {
            final var findFolderResults = service.findFolders(folderId, searchFilter, view);

            if ((findFolderResults != null)
                    && CollectionUtils.isNotEmpty(findFolderResults.getFolders())) {
                log.debug("Find all folders total count {}", findFolderResults.getTotalCount());

                return findFolderResults.getFolders();
            }
        }

        return Collections.emptyList();
    }
}
