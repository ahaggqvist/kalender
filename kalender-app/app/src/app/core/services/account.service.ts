import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import Account from '../../interfaces/account';

@Injectable({providedIn: 'root'})
export class AccountService {
  constructor(private http: HttpClient) {
  }

  findAccount(): Observable<Account> {
    return this.http.get<Account>(`${environment.apiRootUrl}account`);
  }
}
