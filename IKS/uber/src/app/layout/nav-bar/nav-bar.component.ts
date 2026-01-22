import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UpdateLocationComponent } from '../update-location/update-location.component';

/**
 * @title Toolbar overview
 */
@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatSidenavModule, RouterModule],
})
export class NavBarComponent {
  isDriver = false;
  loggedIn: boolean;
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  constructor() {
    this.loggedIn = this.authService.isLoggedIn();
    if (this.loggedIn) {
      const role = this.authService.role();
      this.isDriver = role === 'DRIVER' || role === 'driver';
    }
  }
  openLocationUpdate() {
    const dialogRef = this.dialog.open(UpdateLocationComponent);
  }
}
