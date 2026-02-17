import { Component, output, inject, ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup, FormArray, ReactiveFormsModule } from '@angular/forms';
import { Location } from '../../model/location.model';
import { AuthService } from '../../service/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ride-approx-form',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './ride-approx-form.component.html',
  styleUrl: './ride-approx-form.component.css',
})
export class RideApproxFormComponent {
  private authService = inject(AuthService);
  private cd = inject(ChangeDetectorRef);

  errorMessage: string | null = null;

  endpoints: FormGroup = new FormGroup({
    startLoc: new FormControl(''),
    destination: new FormControl(''),
    stations: new FormArray<FormControl<string>>([]),
  });

  routeOutput = output<Location[]>();
  clearEvent = output<void>();

  private addressCoordinates = new Map<string, { lat: number; lon: number }>();

  get stations() {
    return this.endpoints.get('stations') as FormArray;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  addStation() {
    this.stations.push(new FormControl(''));
  }

  removeStationAt(index: number) {
    this.stations.removeAt(index);
  }

  addLocationFromMap(location: { address: string; lat: number; lon: number }) {
    this.addressCoordinates.set(location.address, { lat: location.lat, lon: location.lon });

    const startLoc = this.endpoints.get('startLoc')?.value;
    const destination = this.endpoints.get('destination')?.value;

    if (!startLoc) {
      this.endpoints.get('startLoc')?.setValue(location.address);
    } else if (!destination) {
      this.endpoints.get('destination')?.setValue(location.address);
    } else {
      if (this.isLoggedIn()) {
        this.addStation();
        this.stations.at(this.stations.length - 1).setValue(destination);

        const destCoords = this.addressCoordinates.get(destination);
        if (destCoords) {
          this.addressCoordinates.set(destination, destCoords);
        }

        this.endpoints.get('destination')?.setValue(location.address);
      } else {
        this.showError('You need to be logged in to add more than 2 locations');
        this.cd.detectChanges();
        return;
      }
    }
  }

  public showError(message: string) {
    this.errorMessage = message;
    this.cd.detectChanges();
    setTimeout(() => {
      this.errorMessage = null;
      this.cd.detectChanges();
    }, 3000);
  }

  removeLocationFromMap(index: number) {
    const totalPoints = 2 + this.stations.length;

    if (index === 0) {
      this.endpoints.get('startLoc')?.setValue('');
    } else if (index === totalPoints - 1) {
      if (this.stations.length > 0) {
        const lastStation = this.stations.at(this.stations.length - 1).value;
        this.endpoints.get('destination')?.setValue(lastStation);
        this.stations.removeAt(this.stations.length - 1);
      } else {
        this.endpoints.get('destination')?.setValue('');
      }
    } else {
      this.stations.removeAt(index - 1);
    }
  }

  clearAllLocations() {
    this.endpoints.get('startLoc')?.setValue('');
    this.endpoints.get('destination')?.setValue('');
    this.stations.clear();
    this.addressCoordinates.clear();
    this.getRoute();
    this.clearEvent.emit();
  }

  public getRoute() {
    let route: Location[] = [];
    let order = 0;

    const startLoc = this.endpoints.controls['startLoc'].value;
    const destination = this.endpoints.controls['destination'].value;

    if (startLoc) {
      const coords = this.addressCoordinates.get(startLoc);
      route.push({
        address: startLoc,
        lat: coords?.lat || 0,
        lon: coords?.lon || 0,
        type: 'pickup',
        index: order,
      });
      order += 1;
    }

    for (let val of this.stations.getRawValue()) {
      if (val) {
        const coords = this.addressCoordinates.get(val);
        route.push({
          address: val,
          lat: coords?.lat || 0,
          lon: coords?.lon || 0,
          type: 'stop',
          index: order,
        });
        order += 1;
      }
    }

    if (destination) {
      const coords = this.addressCoordinates.get(destination);
      route.push({
        address: destination,
        lat: coords?.lat || 0,
        lon: coords?.lon || 0,
        type: 'destination',
        index: order,
      });
    }

    this.routeOutput.emit(route);
    return route;
  }
}
