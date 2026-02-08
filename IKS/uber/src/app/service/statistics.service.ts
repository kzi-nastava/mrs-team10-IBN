import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class StatisticsService {
  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = localStorage.getItem('auth_token');
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
      }),
    };
  }

  getMyStatistics(startDate: string, endDate: string): Observable<any> {
    return this.http.get(`${environment.apiHost}/statistics/my`, {
      params: { startDate, endDate },
      ...this.getAuthHeaders(),
    });
  }

  getAllUsersStatistics(
    startDate: string,
    endDate: string,
    userType: 'drivers' | 'passengers',
  ): Observable<any> {
    return this.http.get(`${environment.apiHost}/statistics/all/${userType}`, {
      params: { startDate, endDate },
      ...this.getAuthHeaders(),
    });
  }

  getUserStatistics(userId: number, startDate: string, endDate: string): Observable<any> {
    return this.http.get(`${environment.apiHost}/statistics/user/${userId}`, {
      params: { startDate, endDate },
      ...this.getAuthHeaders(),
    });
  }

  getUsers(userType: 'drivers' | 'passengers'): Observable<any[]> {
    return this.http.get<any[]>(
      `${environment.apiHost}/statistics/users/${userType}`,
      this.getAuthHeaders(),
    );
  }
}
