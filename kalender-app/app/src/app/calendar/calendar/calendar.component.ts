import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, NavigationExtras, Router} from '@angular/router';
import interactionPlugin from '@fullcalendar/interaction';
import momentPlugin from '@fullcalendar/moment';
import momentTimezonePlugin from '@fullcalendar/moment-timezone';
import timeGridPlugin from '@fullcalendar/timegrid';
import moment from 'moment';
import 'moment/locale/sv';
import {MessageService} from 'primeng/api';
import {FullCalendar} from 'primeng/fullcalendar';
import {Subscription} from 'rxjs';
import {AccountService} from '../../core/services/account.service';
import {AppointmentService} from '../../core/services/appointment.service';
import Account from '../../interfaces/account';
import Appointment from '../../interfaces/appointment';
import Calendar from '../../interfaces/calendar';
import {CalendarService} from '../services/calendar.service';

@Component({
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  @ViewChild('fc') fullcalendarRef: FullCalendar;
  private calendar: Calendar;
  folderId: string;
  isLoading: boolean = true;
  options: any;
  events: Appointment[];
  calendarDate: Date;
  calendarControlDate: Date;
  calendarDateTitle: string;
  calendarNameTitle: string;

  constructor(private accountService: AccountService,
              private messageService: MessageService,
              private appointmentService: AppointmentService,
              private calendarService: CalendarService,
              private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    moment.locale('sv');

    this.folderId = this.route.snapshot.queryParamMap.get('folderId');

    this.subs.add(
      this.calendarService.findCalender(this.folderId).subscribe(
        (calendar: Calendar) => {
          this.calendar = calendar;
          this.calendarNameTitle = calendar.folderName;
        }, () => this.messageService.add({
          severity: 'error',
          detail: 'Error fetching account',
          closable: false
        })
      )
    );

    let defaultDate: Date = moment().toDate();
    const paramDate = this.route.snapshot.queryParamMap.get('date');
    if (paramDate) {
      defaultDate = moment(paramDate).toDate();
    }

    this.options = {
      defaultDate: defaultDate,
      plugins: [timeGridPlugin, interactionPlugin, momentPlugin, momentTimezonePlugin],
      timeFormat: 'HH:mm',
      timeZone: 'Europe/Berlin',
      weekLabel: 'W.',
      weekNumbers: true,
      weekends: true,
      columnHeader: true,
      columnHeaderFormat: {weekday: 'short', day: 'numeric', omitCommas: true},
      height: 'auto',
      contentHeight: 'auto',
      firstDay: 1,
      html: true,
      showNonCurrentDates: false,
      allDaySlot: false,
      titleFormat: {year: 'numeric', month: 'short', day: 'numeric'},
      buttonText: {
        today: 'TODAY',
        month: 'MONTH',
        week: 'WEEK',
        day: 'DAY',
        list: 'LIST'
      },
      scrollTime: '07:00',
      header: false,
      footer: {
        left: '',
        center: '',
        right: ''
      },
      minTime: '04:00',
      maxTime: '24:00',
      slotDuration: '00:15',
      slotLabelFormat: {
        hour: '2-digit',
        minute: '2-digit',
        omitZeroMinute: false,
        meridiem: 'short'
      },
      nowIndicator: true,
      locale: 'en-GB',
      eventBorderColor: 'rgba(111,111,111,0.2) transparent transparent',
      columnFormat: {
        week: 'ddd DD MMM'
      },
      loading: (loading: boolean) => {
        if (!loading) {
          this.setCalendarControlDates();
        }
      },
      eventRender: (info: any) => {
        // Ignore
      },
      events: (info: any, successCallback: any, failureCallback: any) => {
        this.onEvents(info, successCallback);
      },
      datesRender: (event: any) => {
        // Ignore
      },
      dateClick: (event: any) => {
        this.onDateClick(event);
      },
      eventClick: (event: any) => {
        this.onEventClick(event);
      }
    };
  }

  onEvents(info: any, successCallback: any): void {
    const start: string = info.startStr;
    const end: string = moment(info.endStr).subtract(1, 'days').set({
      'hour': 23,
      'minute': 59
    }).format('YYYY-MM-DD[T]HH:mm:SSZ'); //2020-04-27T00:00:00+02:00

    this.subs.add(
      this.appointmentService.findAppointments(this.folderId, start, end).subscribe(
        (appointments: Appointment[]) =>
          successCallback(appointments),
        () => this.messageService.add({
          severity: 'error',
          detail: 'Error fetching events',
          closable: false
        }),
        () => this.isLoading = false)
    );
  }

  async onDateClick(event: any): Promise<void> {
    const navigationExtras: NavigationExtras = {
      queryParams: {folderId: this.calendar.id},
      state: {date: moment(event.dateStr).format('yyyyMMDD')},
      relativeTo: this.route.root
    };
    await this.router.navigate(['appointment'], navigationExtras);
  }

  onEventClick(event: any): void {
    if (event.event?.extendedProps?.attendees.length > 0) {
      this.subs.add(
        this.accountService.findAccount().subscribe(
          (account: Account) => {
            const attendeeMail = event.event.extendedProps.attendees[0].mail;

            if (account.mail === attendeeMail) {
              const navigationExtras: NavigationExtras = {
                queryParams: {folderId: this.calendar.id, appointmentId: event.event.id},
                state: {date: moment(event.event.start).format('yyyyMMDD')},
                relativeTo: this.route.root
              };

              this.router.navigate(['appointment'], navigationExtras);
            } else {
              this.messageService.add({
                severity: 'error',
                detail: 'You can only edit your own events',
                closable: false
              });
            }
          }, () => this.messageService.add({severity: 'error', detail: 'Error fetching account', closable: false}))
      );
    } else {
      this.messageService.add({severity: 'error', detail: 'You cannot edit system events', closable: false});
    }
  }

  onDateChange(event: Date): void {
    this.fullcalendarRef.getCalendar().gotoDate(event);
    this.setCalendarControlDates();
  }

  onClickForward(): void {
    this.fullcalendarRef.getCalendar().next();
    this.setCalendarControlDates();
  }

  onClickBack(): void {
    this.fullcalendarRef.getCalendar().prev();
    this.setCalendarControlDates();
  }

  onClickToday(): void {
    this.fullcalendarRef.getCalendar().today();
    this.setCalendarControlDates();
  }

  private setCalendarControlDate(date: Date): void {
    this.calendarControlDate = date;
  }

  private setCalendarDateTitle(date: Date = this.getStartOfWeek()) {
    this.calendarDateTitle = moment(date).format('D MMM') + ' – ' + moment(date).weekday(7).format('D MMM YYYY');
  }

  private getStartOfWeek(): Date {
    return moment(this.getFullcalenderCurrentDate()).startOf('isoWeek').toDate();
  }

  private getFullcalenderCurrentDate(): Date {
    return this.fullcalendarRef.getCalendar().state.currentDate;
  }

  private setCalendarControlDates(): void {
    this.setCalendarDateTitle();
    this.setCalendarControlDate(this.getStartOfWeek());
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
