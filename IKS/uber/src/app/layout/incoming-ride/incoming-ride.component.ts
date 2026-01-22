import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';
import { MapComponent } from '../../maps/map-basic/map.component';
import { RouteService, RidePayload, RideCancellation } from '../../service/route.service';
import { Station } from '../../model/ride-history.model';

@Component({
  selector: 'app-incoming-ride',
  imports: [MatIconModule, NavBarComponent, MapComponent],
  templateUrl: './incoming-ride.component.html',
  styleUrls: ['./incoming-ride.component.css'],
})
export class IncomingRideComponent {
  private routeService = inject(RouteService);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  route: Station[] = [];
  ride!: RidePayload;

  constructor() {
    this.routeService.getRide().subscribe({
      next: (response) => {
        this.ride = response;
        this.route = [...response.route.stations];
        console.log('Loaded ride:', this.route);
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching ride:', err),
    });
  }

  startRide() {
    if (!this.ride) return;

    const currentTime = new Date().toISOString();
    this.routeService.startRide(this.ride.id, currentTime).subscribe({
      next: () => {
        this.router.navigate([`/tracking-route/${this.ride.id}`]);
      },
      error: (err) => console.error('Error starting ride:', err),
    });
  }

  declineRide(reason: string){
    const cancelledRide: RideCancellation = {
      id: this.ride.id,
      cancellationReason: reason,
      cancelledByDriver: true
    }
    this.routeService.cancelRide(cancelledRide).subscribe({
      next:(res) => this.router.navigate(["/home"])
    })
  }
}
