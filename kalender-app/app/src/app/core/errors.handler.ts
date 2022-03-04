import {HttpErrorResponse} from '@angular/common/http';
import {ErrorHandler, Injectable, Injector, NgZone} from '@angular/core';
import {Router} from '@angular/router';
import {NotificationService} from './services/notification.service';

@Injectable({providedIn: 'root'})
export class ErrorsHandler implements ErrorHandler {
  private router: Router;
  private notificationService: NotificationService;

  constructor(private injector: Injector, private zone: NgZone) {
    this.router = this.injector.get(Router);
    this.notificationService = this.injector.get(NotificationService);
  }

  handleError(error: any): void {
    if (error instanceof HttpErrorResponse) {
      if (!navigator.onLine) {
        this.notificationService.error('No Internet Connection');
      } else {
        this.notificationService.error('An error occurred');
        console.log(`${error.status} - ${error.message}`);
      }
    } else {
      this.zone.run(() => this.router.navigate(['error'], {queryParams: {error: error}}));
    }
    console.log(error);
  }
}
