import { Component, ViewChild, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MapComponent } from '../../maps/map-home/map.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { RideApproxFormComponent } from '../../forms/ride-approx-form/ride-approx-form.component';
import { Location } from '../../model/location.model';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-home',
  imports: [RouterModule, MapComponent, NavBarComponent, RideApproxFormComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  @ViewChild('rideForm') rideForm!: RideApproxFormComponent;
  private authService = inject(AuthService);
  private router = inject(Router);

  routeOutput: Location[] = [];
  estimatedTimeOutput: String = '';

  isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  routeOutputEvent(eventData: Location[]) {
    this.routeOutput = eventData;
  }

  timeEstimationEvent(eventData: string) {
    this.estimatedTimeOutput = eventData;
  }

  onLocationAdded(address: string) {
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

  goToOrder() {
    this.router.navigate(['/order-ride'], {
      state: {
        locations: this.routeOutput,
        estimatedTime: this.estimatedTimeOutput,
      },
    });
  }
}
