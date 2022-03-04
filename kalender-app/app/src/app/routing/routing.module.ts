import {NgModule} from "@angular/core";
import {RouterModule, Routes} from '@angular/router';
import AppointmentDetailGuard from '../appointment-detail/services/appointment-detail-guard.service';
import {CalendarListComponent} from '../calendar/calendar-list/calendar-list.component';
import {ErrorComponent} from '../shared/error/error.component';
import {AppointmentResolver} from '../utils/appointment-resolver';

const appRoutes: Routes = [
  {
    path: '',
    component: CalendarListComponent
  },
  {
    path: 'calendar',
    loadChildren: () => import('../calendar/calendar.module').then(m => m.CalendarModule)
  },
  {
    path: 'appointment',
    canActivate: [AppointmentDetailGuard],
    loadChildren: () => import('../appointment-detail/appointment-detail.module').then(m => m.AppointmentDetailModule),
    resolve: {
      appointment: AppointmentResolver
    }
  },
  {
    path: 'help',
    loadChildren: () => import('../help/help.module').then(m => m.HelpModule)
  },
  {
    path: 'error',
    component: ErrorComponent
  },
  {
    path: '**',
    redirectTo: '/'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes)],
  exports: [RouterModule]
})
export class RoutingModule {
}
