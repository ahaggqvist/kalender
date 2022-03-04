package se.sjuhundrac.kalender.repository;

import com.google.common.collect.Streams;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.service.*;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.sjuhundrac.kalender.model.*;
import se.sjuhundrac.kalender.service.CalendarService;
import se.sjuhundrac.kalender.util.CustomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static microsoft.exchange.webservices.data.core.service.schema.ItemSchema.*;

@Slf4j
@Service
public class AppointmentRepositoryImpl implements AppointmentRepository {
    private static final int MAX_RESULTS = 100;
    private static final UUID PROPERTY_USERNAME_UUID =
            UUID.fromString("16112280-ba9d-48f9-93ff-798f4cbfbbb1");
    private static final String PROPERTY_USERNAME_NAME = "Username";
    private static final String COLOR = "#a6d1f5";

    private final ObjectProvider<ExchangeService> exchangeServiceProvider;
    private final CalendarService calendarService;

    private static PropertySet propertySet;

    public AppointmentRepositoryImpl(
            @NonNull ObjectProvider<ExchangeService> exchangeServiceProvider,
            @NonNull CalendarService calendarService) {
        this.exchangeServiceProvider = exchangeServiceProvider;
        this.calendarService = calendarService;
    }

    static {
        try {
            propertySet =
                    new PropertySet(
                            Id,
                            AppointmentSchema.Start,
                            AppointmentSchema.End,
                            Body,
                            AppointmentSchema.Organizer,
                            AppointmentSchema.Location,
                            Subject,
                            LastModifiedName,
                            AppointmentSchema.RequiredAttendees,
                            AppointmentSchema.Organizer,
                            DateTimeCreated,
                            AppointmentSchema.ICalUid,
                            ParentFolderId,
                            new ExtendedPropertyDefinition(
                                    PROPERTY_USERNAME_UUID, PROPERTY_USERNAME_NAME, MapiPropertyType.String));
            propertySet.setRequestedBodyType(BodyType.Text);
        } catch (final Exception e) {
            // Ignore
        }
    }

    @Override
    @CacheEvict(
            value = {"appointmentsAttendeeCache", "appointmentsCache"},
            allEntries = true)
    public void evictCaches() {
        log.debug("Evicted appointmentsAttendeeCache, appointmentsCache");
    }

    @Override
    @CustomGenerator
    @Cacheable(value = "appointmentsCache", keyGenerator = "customKeyGenerator")
    public List<AppointmentDetail> findAppointments(AppointmentQuery query) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var calendarView =
                    new CalendarView(
                            query.getStartAsDateTime().toDate(),
                            query.getEndAsDateTime().toDate(),
                            MAX_RESULTS);
            calendarView.setPropertySet(new PropertySet(Id));

            final var calendarQuery =
                    CalendarQuery.builder()
                            .folderId(query.getFolderId())
                            .folderName(query.getFolderName())
                            .build();

            final var folder = calendarService.findFolder(calendarQuery);
            final var calendarFolder = CalendarFolder.bind(service, folder.getId());
            if (calendarFolder == null) {
                return Collections.emptyList();
            }

            log.debug(
                    "Folder displayName {}, folderClass {}",
                    calendarFolder.getDisplayName(),
                    calendarFolder.getFolderClass());

            final var results = calendarFolder.findAppointments(calendarView);
            if (CollectionUtils.isEmpty(results.getItems())) {
                return Collections.emptyList();
            }

            // Cast Appointment to Item and back again to avoid iterating over raw types.
            final List<Item> items =
                    Streams.stream(results.iterator()).map(Item.class::cast).toList();
            service.loadPropertiesForItems(items, propertySet);

            final List<Appointment> appointments =
                    Streams.stream(items.iterator())
                            .map(Appointment.class::cast)
                            .toList();

            return appointments.stream()
                    .map(a -> createAppointmentDetail(a, calendarFolder))
                    .toList();
        }
    }

    @Override
    @Cacheable(
            value = "appointmentsAttendeeCache",
            key = "#root.methodName + #query.folderName.replaceAll('\\s', '').toLowerCase()",
            condition = "#query.folderName != null && #query.folderName.length > 0")
    public List<AppointmentDetail> findAppointmentsByUsername(AppointmentQuery query)
            throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var property =
                    new ExtendedPropertyDefinition(
                            PROPERTY_USERNAME_UUID, PROPERTY_USERNAME_NAME, MapiPropertyType.String);
            final var folderId = FolderId.getFolderIdFromString(query.getFolderId());
            final var calendarFolder = CalendarFolder.bind(service, folderId);
            if (calendarFolder == null) {
                return Collections.emptyList();
            }

            final var itemView = new ItemView(MAX_RESULTS);
            final var results =
                    service.findItems(
                            folderId, new SearchFilter.IsEqualTo(property, query.getUsername()), itemView);

            if (CollectionUtils.isNotEmpty(results.getItems())) {
                service.loadPropertiesForItems(results.getItems(), propertySet);
            }

            final List<Appointment> appointments =
                    Streams.stream(results.iterator())
                            .map(Appointment.class::cast)
                            .toList();

            return appointments.stream()
                    .map(a -> createAppointmentDetail(a, calendarFolder))
                    .toList();
        }
    }

    @Override
    public AppointmentDetail findAppointmentById(String id) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var itemId = ItemId.getItemIdFromString(id);
            final var appointment = Appointment.bind(service, itemId, propertySet);
            final var folderId = appointment.getParentFolderId();
            final var folder =
                    service.bindToFolder(folderId, new PropertySet(Id, FolderSchema.DisplayName));
            return createAppointmentDetail(appointment, folder);
        }
    }

    @Override
    @CacheEvict(
            value = {"appointmentsAttendeeCache", "appointmentsCache"},
            allEntries = true)
    public String updateAppointment(AppointmentDetail detail) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var appointment = mapAppointment(detail, service);
            appointment.update(
                    ConflictResolutionMode.AlwaysOverwrite, SendInvitationsOrCancellationsMode.SendToNone);
            return appointment.getId().getUniqueId();
        }
    }

    @Override
    @CacheEvict(
            value = {"appointmentsAttendeeCache", "appointmentsCache"},
            allEntries = true)
    public String saveNewAppointment(AppointmentDetail detail) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var appointmentFolderId = detail.getFolderId();

            final var calendarQuery = CalendarQuery.builder().folderId(appointmentFolderId).build();
            final var folder = calendarService.findFolder(calendarQuery);
            final var folderId = folder.getId();

            final var appointment = mapAppointment(detail, service);
            appointment.save(folderId, SendInvitationsMode.SendToNone);
            return appointment.getId().getUniqueId();
        }
    }

    private Appointment mapAppointment(AppointmentDetail detail, ExchangeService service)
            throws Exception {
        var appointment = new Appointment(service);

        if (detail.isExistingAppointment()) {
            final var itemId = ItemId.getItemIdFromString(detail.getId());
            appointment = Appointment.bind(service, itemId, propertySet);
        }

        // Created by.
        appointment.setExtendedProperty(
                new ExtendedPropertyDefinition(
                        PROPERTY_USERNAME_UUID, PROPERTY_USERNAME_NAME, MapiPropertyType.String),
                detail.getUsername());

        appointment.setStart(detail.getStartDate());
        appointment.setEnd(detail.getEndDate());

        final var location = detail.getExtendedProps().getLocation();
        if (location != null) {
            appointment.setLocation(location);
        }

        final var attendeeDetail =
                new AttendeeDetail(
                        detail.getExtendedProps().getDisplayName(),
                        detail.getExtendedProps().getMail());
        final var attendeeColl = appointment.getRequiredAttendees();
        attendeeColl.add(attendeeDetail.getDisplayName(), attendeeDetail.getMail());

        detail.getExtendedProps().getAttendees().add(attendeeDetail);

        appointment.setSubject(createSubject(detail));
        appointment.setBody(createBody(detail.getExtendedProps().getBody()));

        return appointment;
    }

    @CacheEvict(
            value = {"appointmentsAttendeeCache", "appointmentsCache"},
            allEntries = true)
    public boolean deleteAppointment(Appointment appointment) throws Exception {
        appointment.delete(DeleteMode.MoveToDeletedItems, SendCancellationsMode.SendToNone);
        return true;
    }

    public boolean deleteAppointment(AppointmentDetail detail) throws Exception {
        try (final var service = exchangeServiceProvider.getObject()) {
            final var itemId = ItemId.getItemIdFromString(detail.getId());
            final var appointment = Appointment.bind(service, itemId, propertySet);

            // We only delete a appointment if the appointments username match the delete requests username.
            var appointmentUsername = getAppointmentUsername(appointment);
            if (StringUtils.equalsIgnoreCase(appointmentUsername, detail.getUsername())) {
                log.debug(
                        "Delete appointment with appointment username {}, username {}, itemId {}",
                        appointmentUsername,
                        detail.getUsername(),
                        itemId);
                deleteAppointment(appointment);
                return true;
            }
        }

        return false;
    }

    private String getAppointmentUsername(Appointment appointment) throws ServiceLocalException {
        return (String)
                appointment.getExtendedProperties().getItems().stream()
                        .filter(
                                e -> e.getPropertyDefinition().getPropertySetId().equals(PROPERTY_USERNAME_UUID))
                        .findFirst()
                        .map(ExtendedProperty::getValue)
                        .orElse(null);
    }

    private AppointmentDetail createAppointmentDetail(Appointment appointment, Folder folder) {
        try {
            final var detail = new AppointmentDetail();
            detail.setBackgroundColor(COLOR);
            detail.setId(appointment.getId().getUniqueId());
            detail.setStartDate(appointment.getStart());
            detail.setEndDate(appointment.getEnd());
            detail.setExtendedProps(setExtendedProps(appointment));
            detail.setFolderName(folder.getDisplayName());
            detail.setFolderId(folder.getId().getUniqueId());
            detail.setSubject(appointment.getSubject());

            return detail;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    private ExtendedProps setExtendedProps(Appointment appointment) throws Exception {
        if (appointment == null) {
            return new ExtendedProps();
        }

        final var extendedProps = new ExtendedProps();
        extendedProps.setBody(createBody(appointment));
        extendedProps.setLocation(sanitize(appointment.getLocation()));
        extendedProps.setLastModifiedName(appointment.getLastModifiedName());
        extendedProps.setAttendees(getAttendeeDetails(appointment));
        extendedProps.setDateTimeCreated(appointment.getDateTimeCreated());
        extendedProps.setOrganizerMail(appointment.getOrganizer().getAddress());
        extendedProps.setOrganizerName(appointment.getOrganizer().getName());
        extendedProps.setICalUid(appointment.getICalUid());
        extendedProps.setCreatedBy(getCreatedBy(appointment));

        return extendedProps;
    }

    private String getCreatedBy(Appointment appointment) throws ServiceLocalException {
        return (String)
                appointment.getExtendedProperties().getItems().stream()
                        .filter(
                                e -> e.getPropertyDefinition().getPropertySetId().equals(PROPERTY_USERNAME_UUID))
                        .findFirst()
                        .map(ExtendedProperty::getValue)
                        .orElse("");
    }

    private List<AttendeeDetail> getAttendeeDetails(Appointment appointment)
            throws ServiceLocalException {
        final List<AttendeeDetail> attendeeDetails = new ArrayList<>();

        final var attendeeColl = appointment.getRequiredAttendees();
        if (CollectionUtils.isNotEmpty(attendeeColl.getItems())) {
            for (final Attendee attendee : attendeeColl.getItems()) {
                final var attendeeDetail = new AttendeeDetail(attendee.getName(), attendee.getAddress());
                if (!attendeeDetails.contains(attendeeDetail)) {
                    attendeeDetails.add(attendeeDetail);
                }
            }
        }

        return attendeeDetails;
    }

    private String createSubject(AppointmentDetail detail) {
        return sanitize(detail.getSubject());
    }

    private MessageBody createBody(String body) {
        final var messageBody = new MessageBody();
        messageBody.setBodyType(BodyType.HTML);
        messageBody.setText(sanitize(body));
        return messageBody;
    }

    private String createBody(Appointment appointment) throws Exception {
        if (appointment != null) {
            final var messageBody = appointment.getBody();
            return sanitize(MessageBody.getStringFromMessageBody(messageBody));
        }
        return "";
    }

    private String sanitize(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }

        s =
                StringEscapeUtils.unescapeHtml4(s)
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replaceAll("(?i)<script.*?>.*?</</[^>]+>", "")
                        .replaceAll("(?i)<.*?javascript:.*?>.*?</[^>]+>", "")
                        .replaceAll("(?i)<.*?\\s+on.*?>.*?</</[^>]+>", "")
                        .replaceAll("(?i)&lt;script", "")
                        .replaceAll("(?i)script&gt;", "")
                        .replaceAll("(?i)javascript", "")
                        .replaceAll("(?i)onclick", "")
                        .replaceAll("(?i)onload", "")
                        .replaceAll("(?i)script", "");

        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        return StringUtils.trimToEmpty(Jsoup.clean(policy.sanitize(s), Safelist.none()));
    }
}
