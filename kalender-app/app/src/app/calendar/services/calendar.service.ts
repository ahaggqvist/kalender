import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from 'src/environments/environment';
import Calendar from '../../interfaces/calendar';

@Injectable({providedIn: 'root'})
export class CalendarService {
  constructor(private http: HttpClient) {
  }

  findCalender(id: string): Observable<Calendar> {
    return this.findCalendars().pipe(map(calendars => calendars.find(calendar => calendar.id === decodeURIComponent(id))));
  }

  findCalendars(): Observable<Calendar[]> {
    return this.http.get<Calendar[]>(`${environment.apiRootUrl}calendars`);
  }
}
