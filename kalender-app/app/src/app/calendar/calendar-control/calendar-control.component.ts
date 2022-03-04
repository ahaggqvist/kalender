import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import moment from 'moment';
import 'moment/locale/sv';

@Component({
  selector: 'calendar-control',
  templateUrl: './calendar-control.component.html',
  styleUrls: ['./calendar-control.component.css']
})
export class CalendarControlComponent implements OnInit {
  @Output() forwardEvent = new EventEmitter<void>();
  @Output() backEvent = new EventEmitter<void>();
  @Output() todayEvent = new EventEmitter<void>();
  @Output() dateChangeEvent = new EventEmitter<Date>();
  @Input() selectedDate: Date;
  en: any;

  constructor() {
    // Ignore
  }

  ngOnInit(): void {
    this.en = {
      firstDayOfWeek: 1,
      dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
      dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      dayNamesMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
      monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
      monthNamesShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
      today: 'TODAY',
      clear: 'CLEAR',
      dateFormat: 'yy-mm-dd',
      weekHeader: 'W.'
    };

    moment.locale('en');
    this.selectedDate = moment().startOf('isoWeek').toDate();
  }

  onClickForward(): void {
    this.forwardEvent.emit();
  }

  onClickBack(): void {
    this.backEvent.emit();
  }

  onClickToday(): void {
    this.todayEvent.emit();
  }

  onDateChange(event: Date): void {
    this.dateChangeEvent.emit(event);
  }
}
