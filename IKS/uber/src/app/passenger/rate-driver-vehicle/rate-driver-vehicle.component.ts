import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rate-driver-vehicle',
  imports: [MatIconModule, CommonModule],
  templateUrl: './rate-driver-vehicle.component.html',
  styleUrls: ['./rate-driver-vehicle.component.css'],
})

export class RateDriverVehicleComponent {
  stars = [1, 2, 3, 4, 5];

  driverRating = 0;
  driverHover = 0;

  vehicleRating = 0;
  vehicleHover = 0;

  rateDriver(value: number) {
    this.driverRating = value;
  }

  hoverDriver(value: number) {
    this.driverHover = value;
  }

  rateVehicle(value: number) {
    this.vehicleRating = value;
  }

  hoverVehicle(value: number) {
    this.vehicleHover = value;
  }
}



