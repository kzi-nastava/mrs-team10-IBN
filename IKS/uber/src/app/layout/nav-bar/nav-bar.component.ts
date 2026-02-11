import { ChangeDetectorRef, Component, OnInit, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatBadgeModule } from '@angular/material/badge';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BehaviorSubject } from 'rxjs';
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

  successMessage$ = new BehaviorSubject<string | null>(null);
  errorMessage$ = new BehaviorSubject<string | null>(null);
  panicMessage$ = new BehaviorSubject<string | null>(null);
  unreadCount$ = new BehaviorSubject<number>(0);

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private webSocketService: WebSocketService,
  ) {}

  ngOnInit(): void {
    this.loggedIn = this.authService.isLoggedIn();
    this.role = this.authService.role();

    this.webSocketService.newNotification$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (notif: AppNotification) => {
        if (notif.title?.toUpperCase() === 'PANIC') {
          this.showPanic(notif.content);
        } else {
          this.showSuccess(notif.content);
        }

        const newCount = this.unreadCount$.value + 1;
        this.unreadCount$.next(newCount);
        this.cdr.detectChanges();
      },
      error: (err) => {},
      complete: () => {},
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['']);
    this.loggedIn = false;
    this.unreadCount$.next(0);
    this.cdr.detectChanges();
  }

  resetUnreadCount(): void {
    this.unreadCount$.next(0);
  }

  private playSound(src: string): void {
    try {
      const audio = new Audio(src);
      audio.play().catch((err) => console.log('Audio play failed:', err));
    } catch (err) {
      console.log('Audio error:', err);
    }
  }

  showSuccess(message: string): void {
    this.playSound('youve-been-informed-345.mp3');
    this.successMessage$.next(message);
    this.errorMessage$.next(null);
    this.panicMessage$.next(null);

    setTimeout(() => {
      this.successMessage$.next(null);
      this.cdr.detectChanges();
    }, 10000);

    this.cdr.detectChanges();
  }

  showPanic(message: string): void {
    this.playSound('attention-required-127.mp3');
    this.panicMessage$.next(message);
    this.successMessage$.next(null);
    this.errorMessage$.next(null);

    setTimeout(() => {
      this.panicMessage$.next(null);
      this.cdr.detectChanges();
    }, 10000);

    this.cdr.detectChanges();
  }
}
