import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UpdateLocationComponent } from '../update-location/update-location.component';
import { signal } from '@angular/core';
import { WebSocketService } from '../../service/websocket.service';
import { AppNotification } from '../../service/notification.service';
import { CommonModule } from '@angular/common';

/**
 * @title Toolbar overview
 */
@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    RouterModule,
    CommonModule,
  ],
})
export class NavBarComponent implements OnInit {
  authService: AuthService;
  isDriver = false;
  loggedIn: boolean;
  role: string | null;
  router: Router;
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);
  cdr: ChangeDetectorRef;
  constructor(
    authService: AuthService,
    router: Router,
    cdr: ChangeDetectorRef,
    private webSocketService: WebSocketService,
  ) {
    this.authService = authService;
    this.loggedIn = authService.isLoggedIn();
    this.role = authService.role();
    this.router = router;
    this.cdr = cdr;
  }
  ngOnInit(): void {
    this.webSocketService.newNotification$.subscribe((notif: AppNotification) => {
      console.log(notif);
      this.showSuccess(notif.content);
    });
  }

  logout() {
    this.authService.logout();
    this.loggedIn = false;
    this.router.navigate(['']);
  }

  showSuccess(message: string) {
    this.successMessage.set(message);
    this.errorMessage.set(null);
    setTimeout(() => this.successMessage.set(null), 10000);
  }
}
