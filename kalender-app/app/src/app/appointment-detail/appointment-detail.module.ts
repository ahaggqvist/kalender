import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {AppointmentDetailComponent} from './appointment-detail/appointment-detail.component';

const routes: Routes = [
  {
    path: '',
    component: AppointmentDetailComponent
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
      AppointmentDetailComponent
    ],
  providers: []
})
export class AppointmentDetailModule {
}
