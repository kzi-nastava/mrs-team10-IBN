import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private http = inject(HttpClient)

  loadNotifications(): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(`${environment.apiHost}/notification`).pipe(
      map((data: AppNotification[]) => {
        return data;
      })
    );
  }

  post(data: AppNotification) {
    return this.http.post<AppNotification>(`${environment.apiHost}/notification`, data).pipe(
      map((data: AppNotification) => {
        return data;
      })
    );
  }
}

export interface AppNotificationDTO{
  title: string,
  content: string
}

export interface AppNotification{
  id: number,
  title: string,
  content: string
}