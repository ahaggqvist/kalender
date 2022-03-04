import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {take} from "rxjs/operators";
import {AccountService} from '../../core/services/account.service';
import {AppointmentService} from '../../core/services/appointment.service';
import Account from '../../interfaces/account';
import Appointment from '../../interfaces/appointment';

@Injectable({providedIn: 'root'})
export default class AppointmentDetailGuard implements CanActivate {
  constructor(private accountService: AccountService,
              private appointmentService: AppointmentService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return new Promise((resolve) => {
      if (!route.queryParams.id) {
        resolve(true);
      } else {
        this.appointmentService.findAppointment(route.queryParams.id).pipe(take(1)).subscribe(
          (appointment: Appointment) => {
            if (appointment.extendedProps.attendees.length > 0) {
              const mail = appointment.extendedProps.attendees[0].mail;
              this.accountService.findAccount().subscribe((account: Account) => {
                if (mail === account.mail) {
                  resolve(true);
                } else {
                  resolve(false);
                }
              });
            } else {
              resolve(false);
            }
          });
      }
    });
  }
}
