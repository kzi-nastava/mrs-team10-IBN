import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { WebSocketService } from './service/websocket.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('uber');

  constructor(
    private webSocketService: WebSocketService,
    private router: Router,
  ) {}

  ngOnInit() {
    const token = localStorage.getItem('auth_token');

    if (token && this.isTokenValid(token)) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const userEmail = payload.sub;
        this.webSocketService.connect(userEmail);
      } catch (e) {
        console.error('Failed to auto-connect WebSocket:', e);
        this.clearExpiredToken();
      }
    } else {
      this.clearExpiredToken();
    }
  }

  private isTokenValid(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationTime = payload.exp * 1000;
      return Date.now() < expirationTime;
    } catch (e) {
      return false;
    }
  }

  private clearExpiredToken(): void {
    localStorage.removeItem('auth_token');
    console.warn('Token expired or invalid - cleared from storage');
    //this.router.navigate(['/login']);
  }
}
