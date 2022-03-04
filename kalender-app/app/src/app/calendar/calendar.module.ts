import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {CalendarControlComponent} from './calendar-control/calendar-control.component';
import {CalendarListComponent} from './calendar-list/calendar-list.component';
import {CalendarComponent} from './calendar/calendar.component';

const routes: Routes = [
  {
    path: '',
    component: CalendarComponent
  }
];

@NgModule({
  imports:
    [
      SharedModule,
      RouterModule.forChild(routes)
    ],
  exports: [],
  declarations:
    [
      CalendarListComponent,
      CalendarControlComponent,
      CalendarComponent
    ],
  providers: []
})
export class CalendarModule {
}
