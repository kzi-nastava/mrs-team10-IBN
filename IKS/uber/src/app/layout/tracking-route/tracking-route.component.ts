import { Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';
import { MapComponent } from '../../map/map.component';
import { Signal } from '@angular/core';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user.model';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent, MapComponent, MatDialogModule],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  timeType: String = 'departure';
  time: String = '17:16';
  routeStarted: Boolean = false;
  firstButtonText: String = 'Start';
  protected user: User | null = null;

  constructor(private userService: UserService, private dialog: MatDialog) {
    this.user = this.userService.logged;
  }

  changeState() {
    if (this.routeStarted) {
      this.router.navigate(['/home']);
    }
    this.routeStarted = true;
    this.firstButtonText = 'Finish';
    this.timeType = 'arrival';
  }

  openComplaintDialog() {
    this.dialog.open(ComplaintDialogComponent, {
      width: '90%',
      maxWidth: '420px',
    });
  }
}
