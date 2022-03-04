import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {environment} from '../../../environments/environment';
import Appointment from '../../interfaces/appointment';
import {CustomUrlEncoder} from '../../utils/custom-url-encoder';

@Injectable({providedIn: 'root'})
export class AppointmentService {
  constructor(private http: HttpClient) {
  }

  findAppointment(id: string): Observable<Appointment> {
    if (!id) {
      return of(<Appointment>{});
    }
    return this.http.get<Appointment>(`${environment.apiRootUrl}appointment`, {
      params: new HttpParams({
        encoder: new CustomUrlEncoder()
      }).set('id', decodeURIComponent(id))
    });
  }

  findAppointments(folderId: string, start: string, end: string, timeZone = 'Europe/Berlin'): Observable<Appointment[]> {
    const params = new HttpParams({
      encoder: new CustomUrlEncoder()
    }).set('folderId', decodeURIComponent(folderId)).set('start', start).set('end', end).set('timeZone', timeZone);
    return this.http.get<Appointment[]>(`${environment.apiRootUrl}appointments-folderid`, {params: params});
  }

  findAppointmentsAttendee(folderId: string): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${environment.apiRootUrl}appointments-attendee-folderid`, {
      params: new HttpParams({
        encoder: new CustomUrlEncoder()
      }).set('folderId', decodeURIComponent(folderId))
    });
  }

  saveAppointment(appointment: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(`${environment.apiRootUrl}appointment`, appointment);
  }

  deleteAppointment(appointment: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(`${environment.apiRootUrl}delete-appointment`, appointment);
  }
}
