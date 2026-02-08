import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { NotificationService, AppNotification } from '../../service/notification.service';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { MatIconModule } from '@angular/material/icon';
import { WebSocketService } from '../../service/websocket.service';
import { Subscription, filter } from 'rxjs';

@Component({
  selector: 'app-notification-tab',
  imports: [NavBarComponent, MatIconModule],
  templateUrl: './notification-tab.component.html',
  styleUrl: './notification-tab.component.css',
})
export class NotificationTabComponent implements OnInit, OnDestroy {
  notificationService = inject(NotificationService);
  webSocketService = inject(WebSocketService);

  notifications = signal<AppNotification[]>([]);
  private notificationSubscription?: Subscription;
  private connectionSubscription?: Subscription;

  ngOnInit() {
    this.notificationService.loadNotifications().subscribe({
      next: (res) => {
        const unique = res.filter((notif, i, arr) => arr.findIndex((n) => n.id === notif.id) === i);
        const sorted = unique.sort((a, b) => b.id - a.id);
        this.notifications.set(sorted);
      },
    });

    this.connectionSubscription = this.webSocketService.connectionStatus$
      .pipe(filter((connected) => connected === true))
      .subscribe(() => {
        this.setupNotificationSubscription();
      });

    if (this.webSocketService.isConnected()) {
      this.setupNotificationSubscription();
    }
  }

  private setupNotificationSubscription() {
    this.notificationSubscription = this.webSocketService.newNotification$.subscribe({
      next: (notification) => {
        this.notifications.update((current) => {
          const updated = [notification, ...current];
          return updated;
        });
      },
      error: (err) => {
        console.error('Subscription error:', err);
      },
    });
  }

  ngOnDestroy() {
    this.notificationSubscription?.unsubscribe();
    this.connectionSubscription?.unsubscribe();
  }
}
