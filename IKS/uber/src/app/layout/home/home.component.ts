import { Component, ViewChild, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MapComponent } from '../../maps/map-home/map.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { RideApproxFormComponent } from '../../forms/ride-approx-form/ride-approx-form.component';
import { Location } from '../../model/location.model';
import { AuthService } from '../../service/auth.service';
import { CoordinateDTO, RideService } from '../../service/ride-history.service';
import { firstValueFrom } from 'rxjs';
import { UpdateLocationComponent } from '../update-location/update-location.component';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-home',
  imports: [
    RouterModule,
    MapComponent,
    NavBarComponent,
    RideApproxFormComponent,
    UpdateLocationComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  @ViewChild('rideForm') rideForm!: RideApproxFormComponent;
  private authService = inject(AuthService);
  private router = inject(Router);
  private rideService = inject(RideService);
  routeOutput: Location[] = [];
  estimatedTimeOutput: String = '';

  isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isDriver(): boolean {
    return this.authService.role() === 'driver' || this.authService.role() === 'DRIVER';
  }

  routeOutputEvent(eventData: Location[]) {
    this.routeOutput = eventData;
  }

  timeEstimationEvent(eventData: string) {
    this.estimatedTimeOutput = eventData;
  }

  onLocationAdded(address: CoordinateDTO) {
    this.rideForm.addLocationFromMap(address);
  }

  onLocationRemoved(index: number) {
    this.rideForm.removeLocationFromMap(index);
  }

  onAllLocationsCleared() {
    this.rideForm.clearAllLocations();
  }

  onMaxLocationsReached() {
    this.rideForm.showError('You need to be logged in to add more than 2 locations');
  }

  async goToOrder() {
    try {
      const ongoing = await firstValueFrom(this.rideService.hasOngoingRide());

      if (!ongoing) {
        this.routeOutput = this.rideForm.getRoute();
        this.router.navigate(['/order-ride'], {
          state: {
            locations: this.routeOutput,
            estimatedTime: this.estimatedTimeOutput,
          },
        });
      } else {
        this.rideForm.showError('Please finish your ongoing ride before starting a new one.');
      }
    } catch (err) {
      console.error('Failed to check ongoing ride', err);
      this.rideForm.showError('Could not verify ongoing ride. Please try again.');
    }
  }
}
