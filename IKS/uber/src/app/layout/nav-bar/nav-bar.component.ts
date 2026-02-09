import { ChangeDetectorRef, Component, OnInit, DestroyRef, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatBadgeModule } from '@angular/material/badge';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { AuthService } from '../../service/auth.service';
import { WebSocketService } from '../../service/websocket.service';
import { AppNotification } from '../../service/notification.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatBadgeModule,
  ],
})
export class NavBarComponent implements OnInit {
  private destroyRef = inject(DestroyRef);

  loggedIn = false;
  role: string | null = null;

  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);
  panicMessage = signal<string | null>(null);
  unreadCount = signal<number>(0);

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private webSocketService: WebSocketService,
  ) {}

  ngOnInit(): void {
    this.loggedIn = this.authService.isLoggedIn();
    this.role = this.authService.role();

    this.webSocketService.newNotification$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((notif: AppNotification) => {
        console.log('WS NOTIFICATION:', notif);

        if (notif.title?.toUpperCase() === 'PANIC') {
          this.showPanic(notif.content);
        } else {
          this.showSuccess(notif.content);
        }

        this.unreadCount.update((c) => c + 1);
        this.cdr.detectChanges();
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['']);
    this.loggedIn = false;
    this.unreadCount.set(0);
    this.cdr.detectChanges();
  }

  resetUnreadCount(): void {
    this.unreadCount.set(0);
  }

  private playSound(src: string): void {
    try {
      const audio = new Audio(src);
      audio.play().catch(() => {});
    } catch {}
  }

  showSuccess(message: string): void {
    this.playSound('youve-been-informed-345.mp3');

    this.successMessage.set(message);
    this.errorMessage.set(null);
    this.panicMessage.set(null);

    setTimeout(() => this.successMessage.set(null), 10000);
  }

  showPanic(message: string): void {
    this.playSound('attention-required-127.mp3');

    this.panicMessage.set(message);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    setTimeout(() => this.panicMessage.set(null), 10000);
  }
}
