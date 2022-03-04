import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MessageService, SelectItem} from 'primeng/api';
import {Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import Calendar from '../../interfaces/calendar';
import {CalendarService} from '../services/calendar.service';

@Component({
  templateUrl: './calendar-list.component.html',
  styleUrls: ['./calendar-list.component.css']
})
export class CalendarListComponent implements OnInit, OnDestroy {
  private sub: Subscription;
  calendars: SelectItem[];
  isLoading: boolean = true;

  constructor(private calendarService: CalendarService,
              private messageService: MessageService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.sub = this.calendarService.findCalendars().pipe(map(calendars => {
      return calendars.map(calendar => {
        return {
          label: calendar.folderName,
          value: {
            id: calendar.id,
            name: calendar.folderName
          }
        }
      });
    })).subscribe(selectItems => this.calendars = selectItems,
      () => this.messageService.add({severity: 'error', detail: 'Error fetching calendars', closable: false}),
      () => this.isLoading = false);
  }

  onChangeCalendar(calendar: Calendar): void {
    this.router.navigate(['calendar'], {queryParams: {folderId: calendar.id}, relativeTo: this.route.root});
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
