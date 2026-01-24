import { Component, inject, OnInit, signal,  } from '@angular/core';
import { NotificationService, AppNotification } from '../../service/notification.service';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { MatIconModule } from '@angular/material/icon';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { environment } from '../../../environments/environment';

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
    let ws = new SockJS(`http://localhost:8090/socket`);
    this.stompClient = Stomp.over(ws);
    let that = this;

    this.stompClient.connect({}, function () {
      that.stompClient!.subscribe("/notifications/panic", (message: Stomp.Message) => {
        that.handleResult(message);
      });
    });
  }

  handleResult(message: Stomp.Message){
    const notification: AppNotification = JSON.parse(message.body)
    this.notifications.update((current) => [...current, notification])
  }

  send(){
    const notif: AppNotification = {
      id:100,
      title:"Title 100",
      content: "Content 100"
    }
    this.stompClient?.send("/ws/panic", {}, JSON.stringify(notif))
  }
}

