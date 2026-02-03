import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiHost}/account`;

  constructor(private http: HttpClient) {}

  getDrivers(page: number, size: number): Observable<any> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get(`${this.apiUrl}/drivers`, { params });
  }

  getPassengers(page: number, size: number): Observable<any> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get(`${this.apiUrl}/passengers`, { params });
  }

  blockUser(email: string, reason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/block`, { email, reason });
  }

  unblockUser(email: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/unblock`, { email });
  }
}
