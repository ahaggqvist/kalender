package se.sjuhundrac.kalender.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.service.SyncFolderItemsScope;
import microsoft.exchange.webservices.data.core.exception.misc.ArgumentException;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.ItemChange;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.model.AppointmentQuery;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class StateServiceImpl implements StateService {
    private static final int MAX_CHANGES = 512;
    private final ObjectProvider<ExchangeService> exchangeServiceProvider;
    private Map<String, String> syncStateStore;

    public StateServiceImpl(@NonNull ObjectProvider<ExchangeService> exchangeServiceProvider) {
        this.exchangeServiceProvider = exchangeServiceProvider;
    }

    @PostConstruct
    public void init() {
        syncStateStore = new HashedMap<>();
    }

    @Override
    public boolean isAppointmentCacheEviction(AppointmentQuery query) {
        final var folderId = query.getFolderId();
        if (StringUtils.isBlank(folderId)) {
            throw new ArgumentException("FolderId is null");
        }

        final var changeCollection = getAppointmentSyncState(query, syncStateStore.get(folderId));

        log.debug("Changes {}", changeCollection.getCount());
        if (changeCollection.getCount() == 0) {
            return false;
        }

        syncStateStore.put(folderId, changeCollection.getSyncState());
        return true;
    }

    protected ChangeCollection<ItemChange> getAppointmentSyncState(
            AppointmentQuery query, String syncState) {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var folderId = FolderId.getFolderIdFromString(query.getFolderId());

            return service.syncFolderItems(
                    folderId,
                    new PropertySet(BasePropertySet.IdOnly),
                    null,
                    MAX_CHANGES,
                    SyncFolderItemsScope.NormalItems,
                    syncState);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
