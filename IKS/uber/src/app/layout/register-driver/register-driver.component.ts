import { Component, OnInit, ViewChild } from '@angular/core';
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
  @ViewChild(UserFormComponent) userForm!: UserFormComponent;
  @ViewChild(VehicleFormComponent) vehicleForm!: VehicleFormComponent;

  newDriverData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
    image: '',
  };

  emptyDriverData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
    image: '',
  };

  newVehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    plate: '',
    seatNumber: 4,
    babySeat: false,
    petFriendly: false,
  };

  emptyVehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    plate: '',
    seatNumber: 4,
    babySeat: false,
    petFriendly: false,
  };

  successMessage: string | null = null;
  errorMessage: string | null = null;

  ngOnInit() {}

  registerDriver() {
    this.userForm.formTouched = true;
    this.userForm.touchedFields = {
      firstName: true,
      lastName: true,
      address: true,
      phone: true,
      email: true,
    };

    this.vehicleForm.formTouched = true;
    this.vehicleForm.touchedFields = {
      model: true,
      type: true,
      licensePlate: true,
      seatNumber: true,
    };

    const isUserFormValid = this.userForm.isFormValid();
    const isVehicleFormValid = this.vehicleForm.isFormValid();

    if (!isUserFormValid || !isVehicleFormValid) {
      this.showError('Please fill in all required fields correctly.');
      return;
    }

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
      image: '',
    };

    this.newVehicleData = {
      model: '',
      type: 'standard',
      plate: '',
      seatNumber: 4,
      babySeat: false,
      petFriendly: false,
    };

    if (this.userForm) {
      this.userForm.formTouched = false;
      this.userForm.touchedFields = {
        firstName: false,
        lastName: false,
        address: false,
        phone: false,
        email: false,
      };
    }
    if (this.vehicleForm) {
      this.vehicleForm.formTouched = false;
      this.vehicleForm.touchedFields = {
        model: false,
        type: false,
        licensePlate: false,
        seatNumber: false,
      };
    }
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
