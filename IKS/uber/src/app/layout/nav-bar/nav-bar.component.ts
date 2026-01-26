import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
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
  role: string | null;
  router: Router;
  constructor(authService: AuthService, router: Router) {
    this.loggedIn = authService.isLoggedIn();
    this.role = authService.role();
    this.router = router;
  }

  logout() {
    localStorage.clear();
    this.loggedIn = false;
    this.router.navigate(['/home']);
  }
}
