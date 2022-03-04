import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {AppointmentService} from '../core/services/appointment.service';
import Appointment from '../interfaces/appointment';

@Injectable({providedIn: 'root'})
export class AppointmentResolver implements Resolve<Appointment> {
  constructor(private appointmentService: AppointmentService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Appointment> {
    return this.appointmentService.findAppointment(route.queryParams.appointmentId);
  }
}
