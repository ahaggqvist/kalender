import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';

@Injectable()
export class ErrorsInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, handler: HttpHandler): Observable<HttpEvent<any>> {
    return handler.handle(request)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          return throwError(error);
        })
      )
  }
}
