import { Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';
import { TrackingMapComponent } from '../../maps/tracking-map/tracking-map.component';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user.model';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';
import { RouteService } from '../../service/route.service';
import { Location } from '../../model/location.model';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent, TrackingMapComponent, MatDialogModule],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  routeService: RouteService = inject(RouteService)
  userService: UserService = inject(UserService)
  route: Location[] = this.routeService.route
  passed: number = 0;
  estimatedTime?: string;
  subtitleText: String = 'Waiting for departure...';
  routeStarted: Boolean = false;
  firstButtonText: String = 'Start';
  protected user: User | null = null;

  constructor(private dialog: MatDialog) {
    let logged = sessionStorage.getItem('loggedUser');
    if (logged != null) {
      this.user = JSON.parse(logged) as User;
    } else {
      this.user = {
        "id":1,
        "name":"Petar",
        "lastName":"Petrović",
        "role":"driver",
        "phone":"000",
        "image":"",
      }
    }
  }

  setTimeEvent(eventData: string){
    this.estimatedTime = eventData;
  }

  passStationEvent(eventData: number){
    this.passed = eventData;
    console.log(this.passed)
  }

  changeState() {
    if (this.routeStarted) {
      
    }
    this.routeStarted = true;
    this.firstButtonText = 'Finish';
    this.subtitleText = "Estimated arrival time: " + this.estimatedTime;
  }

  openComplaintDialog() {
    this.dialog.open(ComplaintDialogComponent, {
      width: '90%',
      maxWidth: '420px',
    });
  }
}
