import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import Room from '../../interfaces/room';

@Injectable({providedIn: 'root'})
export class RoomService {
  constructor(private http: HttpClient) {
  }

  findRooms(location: string): Observable<Room[]> {
    return this.http.get<Room[]>(`${environment.apiRootUrl}rooms`, {
      params: new HttpParams().set('location', location)
    });
  }
}
