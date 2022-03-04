import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {ErrorHandler, NgModule} from '@angular/core';
import {MessageService} from 'primeng/api';
import {ErrorsHandler} from './errors.handler';
import {ErrorsInterceptor} from './errors.interceptor';

@NgModule({
  imports:
    [
      CommonModule
    ],
  exports:
    [
      CommonModule
    ],
  declarations: [],
  providers:
    [
      {
        provide: ErrorHandler,
        useClass: ErrorsHandler
      },
      {
        provide: HTTP_INTERCEPTORS,
        useClass: ErrorsInterceptor,
        multi: true
      },
      MessageService
    ],
})
export class CoreModule {
}
