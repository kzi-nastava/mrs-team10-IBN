import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';
import { TrackingMapComponent } from '../../maps/tracking-map/tracking-map.component';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user.model';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';
import { RouteService } from '../../service/route.service';
import { Location } from '../../model/location.model';
import { Station } from '../../model/ride-history.model';

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
  private cdr = inject(ChangeDetectorRef);
  route: Station[] = []
  passed: number = 0;
  currentLocation?: TrackingData;
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
    this.routeService.getRide().subscribe({
      next: (response) => {
        this.route = [...response.route.stations];
        console.log(this.route)
        this.cdr.detectChanges();
      }
    })
  }

  setTimeEvent(eventData: string){
    this.estimatedTime = eventData;
  }

  passStationEvent(eventData: number){
    this.passed = eventData;
  }

  getLocationEvent(eventData: TrackingData){
    this.currentLocation = eventData;
    console.log(this.currentLocation);
  }

  changeState() {
    if (this.passed == 1 && !this.routeStarted) {
      this.routeStarted = true;
      this.firstButtonText = 'Finish';
      this.subtitleText = "Estimated arrival time: " + this.estimatedTime;
    } else if (this.passed == this.route.length) {

    } else if (this.passed > 0 && this.passed < this.route.length){

    }
  }

  openComplaintDialog() {
    this.dialog.open(ComplaintDialogComponent, {
      width: '90%',
      maxWidth: '420px',
    });
  }
}

export interface TrackingData{
  lat:number,
  lon:number,
  address:string,
}