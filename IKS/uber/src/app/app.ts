import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WebSocketService } from './service/websocket.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('uber');

  constructor(private webSocketService: WebSocketService) {}

  ngOnInit() {
    const token = localStorage.getItem('auth_token');

    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const userEmail = payload.sub;
        this.webSocketService.connect(userEmail);
      } catch (e) {
        console.error('Failed to auto-connect WebSocket:', e);
      }
    } else {
      console.warn('No auth token found - WebSocket will not connect');
    }
  }
}
