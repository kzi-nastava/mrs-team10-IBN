import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { ActivatedRoute, Router } from '@angular/router';
import { TrackingMapComponent } from '../../maps/tracking-map/tracking-map.component';
import { AuthService } from '../../service/auth.service';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';
import { RouteService } from '../../service/route.service';
import { Station } from '../../model/ride-history.model';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent, TrackingMapComponent, MatDialogModule],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  urlRoute: ActivatedRoute = inject(ActivatedRoute)
  routeService: RouteService = inject(RouteService)
  userService: AuthService = inject(AuthService)
  private cdr = inject(ChangeDetectorRef);
  rideId?: number;
  route: Station[] = []
  passed: number = 0;
  distance: number = 0
  currentLocation?: TrackingData;
  estimatedTime?: string;
  subtitleText: String = 'Waiting for departure...';
  routeStarted: Boolean = false;
  protected role: string | null;
  panicWarningDisplay: boolean = false;

  constructor(authService: AuthService, private http: HttpClient,
          private dialog: MatDialog
  ){
      this.role = authService.role();
      this.routeService.getOngoingRide(this.urlRoute.snapshot.paramMap.get('rideId')!).subscribe({
        next: (response) => {
          this.route = [...response.route.stations];
          this.rideId = response.id
          this.cdr.detectChanges();
        }
      })
      // this.setRideId();
    }

  // setRideId() {
  //   this.http
  //     .get<Ride>(`${environment.apiHost}/rides/trackingRidePassenger`)
  //     .subscribe({
  //       next: (ride) => {
  //         this.rideId = ride.id;
  //         console.log(this.rideId);
  //       },
  //       error: (err) => {
  //         console.error('Failed to fetch ride', err);
  //       }
  //     });
  // }

  setTimeEvent(eventData: string){
    this.estimatedTime = eventData;
    this.subtitleText = "Estimated time arrival in: " + this.estimatedTime;
  }

  passStationEvent(eventData: number){
    this.passed = eventData;
  }

  getLocationEvent(eventData: TrackingData){
    this.currentLocation = eventData;
  }

  getDistanceEvent(eventData: number){
    this.distance = eventData
  }

  // This function updates the ui on one device
  // after a state change happened on another device
  stateChangeEvent(eventData: string){
    if(eventData == "Finished"){
      // ride finishing logic
      this.router.navigate(["/home"])
    } else if (eventData == "Panic") {
      this.subtitleText = "Panic signal has been broadcast. Help is on the way. Please remain calm."
      this.cdr.detectChanges()
    }
  }

  changeState() {
    const now = new Date();
    if (this.passed == this.route.length) {
      this.routeService.finishRide(this.rideId!, now.toISOString()).subscribe({
        next: (res) => {
          // ride finishing logic
          this.router.navigate(["/home"])
        }
      });
    } else {
      this.routeService.stopRide(this.rideId!, this.passed, now.toISOString(), this.currentLocation!, this.distance).subscribe({
        next: (res) => {
          // ride finishing logic
          this.router.navigate(["/home"])
        }
      });
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

  showPanicWarning(){
    this.panicWarningDisplay = true;
  }

  panic(){
    const now = new Date();
    this.routeService.panic(this.rideId!, this.passed, now.toISOString(), this.currentLocation!)
    .subscribe({
      next: (res) => {
        this.subtitleText = "Panic signal has been broadcast. Help is on the way. Please remain calm."
        this.cdr.detectChanges()
      }
    })
  }
}

export interface TrackingData{
  lat:number,
  lon:number,
  address:string,
}
