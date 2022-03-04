import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {CalendarModule} from 'primeng/calendar';
import {CardModule} from 'primeng/card';
import {CheckboxModule} from 'primeng/checkbox';
import {DropdownModule} from 'primeng/dropdown';
import {FullCalendarModule} from 'primeng/fullcalendar';
import {InputTextModule} from 'primeng/inputtext';
import {MenuModule} from 'primeng/menu';
import {MessageModule} from 'primeng/message';
import {MessagesModule} from 'primeng/messages';
import {PanelModule} from 'primeng/panel';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import {RadioButtonModule} from 'primeng/radiobutton';
import {ToastModule} from 'primeng/toast';
import {TooltipModule} from 'primeng/tooltip';
import {ErrorComponent} from './error/error.component';
import {HeaderComponent} from './header/header.component';

@NgModule({
  imports:
    [
      CommonModule,
      CardModule,
      ToastModule,
      MenuModule,
      TooltipModule,
      ButtonModule,
      RadioButtonModule,
      MessagesModule,
      MessageModule,
      ProgressSpinnerModule,
      CalendarModule,
      FullCalendarModule,
      FormsModule,
      DropdownModule,
      PanelModule,
      InputTextModule,
      CheckboxModule
    ],
  exports:
    [
      HeaderComponent,
      ErrorComponent,
      CommonModule,
      CardModule,
      ToastModule,
      MenuModule,
      TooltipModule,
      ButtonModule,
      RadioButtonModule,
      MessagesModule,
      MessageModule,
      ProgressSpinnerModule,
      CalendarModule,
      FullCalendarModule,
      FormsModule,
      DropdownModule,
      PanelModule,
      InputTextModule,
      CheckboxModule
    ],
  declarations:
    [
      HeaderComponent,
      ErrorComponent
    ],
  providers: []
})
export class SharedModule {
}
