import { Component, inject, OnDestroy, OnInit, signal,  } from '@angular/core';
import { NotificationService, AppNotification, AppNotificationDTO } from '../../service/notification.service';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { MatIconModule } from '@angular/material/icon';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-notification-tab',
  imports: [NavBarComponent, MatIconModule],
  templateUrl: './notification-tab.component.html',
  styleUrl: './notification-tab.component.css',
})
export class NotificationTabComponent implements OnInit{
  notificationService: NotificationService = inject(NotificationService);
  notifications = signal<AppNotification[]>([])
  stompClient: Stomp.Client | undefined;

  ngOnInit(){
    this.notificationService.loadNotifications().subscribe({
      next: (res) => this.notifications.set(res)
    })
    let ws = new SockJS(`${environment.socketHost}`);
    this.stompClient = Stomp.over(ws);
    let that = this;

    this.stompClient.connect({}, function () {
      that.stompClient!.subscribe("/notifications/admin", (message: Stomp.Message) => {
        that.handleResult(message);
      });
    });
  }

  handleResult(message: Stomp.Message){
    const notification: AppNotification = JSON.parse(message.body)
    this.notifications.update((current) => [...current, notification])
  }

  
}

