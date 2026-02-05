import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Subject } from 'rxjs';
import { AppNotification, AppNotificationDTO } from './notification.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient: Stomp.Client | undefined;

  public newNotification$ = new Subject<AppNotification>();
  public incomingRide$ = new Subject<any>();

  public connectionStatus$ = new BehaviorSubject<boolean>(false);

  constructor(
    private router: Router,
    private zone: NgZone,
  ) {}

  connect(userEmail: string) {
    if (this.stompClient?.connected) {
      return;
    }

    const ws = new SockJS(environment.socketHost);
    this.stompClient = Stomp.over(ws);

    const token = localStorage.getItem('auth_token');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    this.stompClient.connect(
      headers,
      (frame) => {
        this.zone.run(() => {
          this.connectionStatus$.next(true);

          const notifSubscription = '/user/queue/notifications';

          this.stompClient!.subscribe(notifSubscription, (message) => {
            this.zone.run(() => {
              try {
                const notification: AppNotification = JSON.parse(message.body);

                this.newNotification$.next(notification);
              } catch (err) {
                console.error('Error parsing notification:', err);
              }
            });
          });

          const rideSubscription = '/topic/ride/' + userEmail;

          this.stompClient!.subscribe(rideSubscription, (message) => {
            this.zone.run(() => {
              try {
                const payload = JSON.parse(message.body);
                this.incomingRide$.next(payload);
                this.router.navigate(['/incoming-ride']);
              } catch (err) {
                console.error('Error parsing ride message:', err);
              }
            });
          });
        });
      },
      (error) => {
        this.zone.run(() => {
          this.connectionStatus$.next(false);
        });
      },
    );
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        this.connectionStatus$.next(false);
      });
      this.stompClient = undefined;
    }
  }

  isConnected(): boolean {
    const connected = this.stompClient?.connected || false;
    return connected;
  }

  sendPanic(notif: AppNotificationDTO) {
    this.stompClient?.send('/ws/panic', {}, JSON.stringify(notif));
  }
}
