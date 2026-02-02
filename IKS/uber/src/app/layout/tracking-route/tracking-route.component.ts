import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { ActivatedRoute, Router } from '@angular/router';
import { TrackingMapComponent } from '../../maps/tracking-map/tracking-map.component';
import { AuthService } from '../../service/auth.service';
import {
  MatDialogModule,
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { ComplaintDialogComponent } from '../../passenger/complaint-dialog/complaint-dialog.component';
import { RouteService } from '../../service/route.service';
import { Station } from '../../model/ride-history.model';
import { HttpClient } from '@angular/common/http';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { AppNotification, AppNotificationDTO } from '../../service/notification.service';
import { WebSocketService } from '../../service/websocket.service';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent, TrackingMapComponent, MatDialogModule],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  urlRoute: ActivatedRoute = inject(ActivatedRoute);
  routeService: RouteService = inject(RouteService);
  userService: AuthService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  rideId?: number;
  route: Station[] = [];
  passed: number = 0;
  distance: number = 0;
  currentLocation?: TrackingData;
  estimatedTime?: string;
  subtitleText: String = 'Waiting for departure...';
  routeStarted: Boolean = false;
  protected role: string | null;
  panicWarningDisplay: boolean = false;
  isLoading: boolean = true;

  constructor(
    authService: AuthService,
    private http: HttpClient,
    private dialog: MatDialog,
    private webSocketService: WebSocketService,
  ) {
    this.role = authService.role();
    this.loadRoute();
  }

  private loadRoute(): void {
    if (this.role == 'passenger') {
      this.routeService.getTrackingRide().subscribe({
        next: (ride) => {
          this.rideId = ride.id;
          this.route = ride.route?.stations || [];
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to fetch tracking ride:', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    } else {
      const rideIdParam = this.urlRoute.snapshot.paramMap.get('rideId');
      if (rideIdParam) {
        this.routeService.getOngoingRide(rideIdParam).subscribe({
          next: (response) => {
            this.route = response.route?.stations || [];
            this.rideId = response.id;
            this.isLoading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to fetch ongoing ride:', err);
            this.isLoading = false;
            this.cdr.detectChanges();
          },
        });
      }
    }
  }

  setTimeEvent(eventData: string) {
    this.estimatedTime = eventData;
    this.subtitleText = 'Estimated time arrival in: ' + this.estimatedTime;
  }

  passStationEvent(eventData: number) {
    this.passed = eventData;
  }

  getLocationEvent(eventData: TrackingData) {
    this.currentLocation = eventData;
  }

  getDistanceEvent(eventData: number) {
    this.distance = eventData;
  }

  stateChangeEvent(eventData: { status: string; location: TrackingData | null }) {
    if (eventData.status == 'Finished') {
      this.router.navigate(['/home']);
    } else if (eventData.status == 'Panic') {
      const notif: AppNotificationDTO = {
        title: 'PANIC',
        content: `Emergency in ${this.currentLocation?.address}`,
      };
      this.webSocketService.sendPanic(notif);
      this.subtitleText =
        'Panic signal has been broadcast. Help is on the way. Please remain calm.';
      this.cdr.detectChanges();
    }
  }

  changeState(location: TrackingData) {
    const now = new Date();
    if (this.passed == this.route.length) {
      this.routeService.finishRide(this.rideId!, now.toISOString()).subscribe({
        next: (res) => {
          this.router.navigate(['/home']);
        },
      });
    } else {
      this.routeService
        .stopRide(this.rideId!, this.passed, now.toISOString(), location, this.distance)
        .subscribe({
          next: (res) => {
            this.router.navigate(['/incoming-ride']);
          },
        });
    }
  }

  openComplaintDialog() {
    this.dialog.open(ComplaintDialogComponent, {
      width: '90%',
      maxWidth: '420px',
      data: { rideId: this.rideId },
    });
  }

  showPanicWarning() {
    this.panicWarningDisplay = true;
  }

  panic() {
    const now = new Date();
    this.routeService
      .panic(this.rideId!, this.passed, now.toISOString(), this.currentLocation!)
      .subscribe({
        next: (res) => {
          const notif: AppNotificationDTO = {
            title: 'PANIC',
            content: `Emergency in ${this.currentLocation?.address}`,
          };
          this.subtitleText =
            'Panic signal has been broadcast. Help is on the way. Please remain calm.';
          this.cdr.detectChanges();
        },
      });
  }
}

export interface TrackingData {
  lat: number;
  lon: number;
  address: string;
}
