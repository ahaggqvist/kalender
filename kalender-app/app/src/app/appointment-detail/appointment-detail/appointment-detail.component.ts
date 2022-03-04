import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import moment from 'moment';
import {MessageService} from 'primeng/api';
import {SelectItem} from 'primeng/api/selectitem';
import {Subscription} from 'rxjs';
import {AppointmentService} from '../../core/services/appointment.service';
import Appointment from '../../interfaces/appointment';
import {RoomService} from '../services/room.service';

@Component({
  templateUrl: './appointment-detail.component.html',
  styleUrls: ['./appointment-detail.component.css']
})
export class AppointmentDetailComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  private appointment: Appointment;
  startTime: Date;
  endTime: Date;
  rooms: SelectItem[];
  selectedRoom: string;
  isLoading: boolean = false;
  date: string;
  folderId: string;
  title: string = '';
  body: string = '';
  start: string;
  saveClicked = true;

  constructor(private roomService: RoomService,
              private messageService: MessageService,
              private appointmentService: AppointmentService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    moment.locale('sv');

    // Uncomment if you wish to render an list of locations
    /*this.subs.add(
      this.roomService.findRooms('').pipe(map(rooms => {
        return rooms.map(room => {
          return {
            label: room.name,
            value: room.mail
          }
        });
      })).subscribe(selectItems => this.rooms = selectItems)
    );*/

    this.date = moment(window.history.state.date).format('yyyyMMDD');

    const m = moment();
    this.startTime = moment(this.date).set({'hour': m.hour(), 'minute': m.minute()}).toDate();
    this.endTime = moment(this.date).set({'hour': m.hour(), 'minute': m.minute()}).toDate();

    this.appointment = this.route.snapshot.data['appointment'];
    this.folderId = this.route.snapshot.queryParamMap.get('folderId');
    this.start = moment(this.date).format('yyyy-MM-DD');

    if (Object.keys(this.appointment).length !== 0) {
      this.title = this.appointment.title;
      this.body = this.appointment.extendedProps.body;
      this.selectedRoom = this.appointment.extendedProps.location;
      this.start = moment(this.appointment.start).format('yyyy-MM-DD');
      this.startTime = moment(this.appointment.start).toDate();
      this.endTime = moment(this.appointment.end).toDate();
    }
  }

  onSubmit(value: any) {
    this.isLoading = true;

    let startDateTime = moment(this.startTime);
    let endDateTime = moment(this.endTime);

    const start = startDateTime.format("YYYY-MM-DD[T]HH:mm:ss");
    const end = endDateTime.format("YYYY-MM-DD[T]HH:mm:ss");

    let newAppointment: Appointment = {
      folderId: this.folderId,
      extendedProps: {
        location: value.selectedRoom,
        body: value.body
      },
      start: start,
      end: end,
      title: this.title
    };

    if (Object.keys(this.appointment).length !== 0) {
      const existingAppointment: Appointment = <Appointment>{
        ...this.appointment,
        extendedProps: {
          body: value.body,
          location: value.selectedRoom
        },
        title: this.title,
        start: start,
        end: end
      };

      newAppointment = {...existingAppointment}
    }

    if (this.saveClicked) {
      this.subs.add(
        this.appointmentService.saveAppointment(newAppointment).subscribe(
          () => this.router.navigate(['calendar'], {
            queryParams: {
              folderId: this.folderId,
              date: this.date
            },
            relativeTo: this.route.root
          }),
          () => this.messageService.add({
            severity: 'error',
            detail: 'Error saving event',
            closable: false
          }),
          () => this.isLoading = false)
      );
    } else {
      this.subs.add(
        this.appointmentService.deleteAppointment(this.appointment).subscribe(
          () => this.router.navigate(['calendar'], {
            queryParams: {
              folderId: this.folderId,
              date: this.date
            },
            relativeTo: this.route.root
          }),
          () => this.messageService.add({
            severity: 'error',
            detail: 'Error deleting event',
            closable: false
          }),
          () => this.isLoading = false)
      );
    }
  }

  onClickSave(): void {
    this.saveClicked = true;
  }

  onClickDelete(): void {
    this.saveClicked = false;
  }

  onClickCancel(): void {
    this.router.navigate(['calendar'], {
      queryParams: {
        folderId: this.folderId, date: this.date
      }, relativeTo: this.route.root
    });
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
