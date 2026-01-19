import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';
import { TrackingMapComponent } from '../../maps/tracking-map-passenger/tracking-map.component';
import { AuthService } from '../../service/auth.service';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';
import { RouteService } from '../../service/route.service';
import { Station } from '../../model/ride-history.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Ride } from '../../model/ride-history.model';
import { Inject } from '@angular/core';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent, TrackingMapComponent, MatDialogModule],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  routeService: RouteService = inject(RouteService)
  userService: AuthService = inject(AuthService)
  private cdr = inject(ChangeDetectorRef);
  rideId?: number;
  route: Station[] = []
  passed: number = 0;
  currentLocation?: TrackingData;
  estimatedTime?: string;
  subtitleText: String = 'Waiting for departure...';
  routeStarted: Boolean = false;
  firstButtonText: String = 'Start';
  protected role: string | null;

  constructor(authService: AuthService, private http: HttpClient,
          private dialog: MatDialog
  ){
      this.role = authService.role();
      // this.routeService.getRide().subscribe({
      //   next: (response) => {
      //     this.route = [...response.route.stations];
      //     this.rideId = response.id
      //     this.cdr.detectChanges();
      //   }
      // })
      this.setRideId();
    }

  setRideId() {
    this.http
      .get<Ride>(`${environment.apiHost}/rides/trackingRidePassenger`)
      .subscribe({
        next: (ride) => {
          this.rideId = ride.id;
          console.log(this.rideId);
        },
        error: (err) => {
          console.error('Failed to fetch ride', err);
        }
      });
  }

  setTimeEvent(eventData: string){
    this.estimatedTime = eventData;
    this.subtitleText = "Estimated time arrival in: " + this.estimatedTime;
  }

  passStationEvent(eventData: number){
    this.passed = eventData;
  }

  getLocationEvent(eventData: TrackingData){
    this.currentLocation = eventData;
    console.log(this.currentLocation);
  }

  changeState() {
    const now = new Date();
    if (this.passed == 1 && !this.routeStarted) {
      this.routeStarted = true;
      this.firstButtonText = 'Finish';
      this.subtitleText = "Estimated arrival time: " + this.estimatedTime;
      this.routeService.startRide(this.rideId!, now.toISOString());
    } else if (this.passed == this.route.length) {
      this.routeService.finishRide(this.rideId!, now.toISOString());
      this.router.navigate(["/home"])
    } else if (this.passed > 0 && this.passed < this.route.length){
      this.routeService.stopRide(this.rideId!, this.passed, now.toISOString(), this.currentLocation!)
      this.router.navigate(["/home"])
    }
  }

  openComplaintDialog() {
    console.log(this.rideId)
    this.dialog.open(ComplaintDialogComponent, {
      width: '90%',
      maxWidth: '420px',
      data: {rideId: this.rideId}
    });
  }
}

export interface TrackingData{
  lat:number,
  lon:number,
  address:string,
}