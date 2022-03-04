import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppComponent} from './app.component';
import {AppointmentDetailModule} from './appointment-detail/appointment-detail.module';
import {CalendarModule} from './calendar/calendar.module';
import {CoreModule} from './core/core.module';
import {HelpModule} from './help/help.module';
import {RoutingModule} from './routing/routing.module';
import {SharedModule} from './shared/shared.module';

@NgModule({
  declarations:
    [
      AppComponent
    ],
  imports:
    [
      BrowserModule,
      BrowserAnimationsModule,
      HttpClientModule,
      HttpClientXsrfModule,
      CoreModule,
      RoutingModule,
      SharedModule,
      CalendarModule,
      AppointmentDetailModule,
      HelpModule
    ],
  providers: [],
  bootstrap:
    [
      AppComponent
    ]
})
export class AppModule {
}
