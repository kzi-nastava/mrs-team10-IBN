import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { UserFormComponent, UserFormData } from '../../forms/user-form/user-form.component';
import {
  VehicleFormComponent,
  VehicleFormData,
} from '../../forms/vehicle-form/vehicle-form.component';

@Component({
  selector: 'app-register-driver',
  standalone: true,
  imports: [CommonModule, NavBarComponent, UserFormComponent, VehicleFormComponent],
  templateUrl: './register-driver.component.html',
  styleUrls: ['./register-driver.component.css'],
})
export class RegisterDriverComponent implements OnInit {
  newDriverData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  emptyDriverData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  newVehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    licensePlate: '',
    seats: 4,
    babyTransport: false,
    petTransport: false,
  };

  successMessage: string | null = null;
  errorMessage: string | null = null;

  ngOnInit() {}

  registerDriver() {
    const registrationData = {
      driver: this.newDriverData,
      vehicle: this.newVehicleData,
    };

    console.log('Registering driver with vehicle:', registrationData);

    this.showSuccess('Driver and vehicle registered successfully!');

    setTimeout(() => {
      this.resetForms();
    }, 2000);
  }

  resetForms() {
    this.newDriverData = {
      firstName: '',
      lastName: '',
      address: '',
      phone: '',
      email: '',
    };

    this.newVehicleData = {
      model: '',
      type: 'standard',
      licensePlate: '',
      seats: 4,
      babyTransport: false,
      petTransport: false,
    };
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.errorMessage = null;
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.successMessage = null;
    setTimeout(() => {
      this.errorMessage = null;
    }, 3000);
  }
}
