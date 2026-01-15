import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { UserFormComponent, UserFormData } from '../../forms/user-form/user-form.component';
import {
  VehicleFormComponent,
  VehicleFormData,
} from '../../forms/vehicle-form/vehicle-form.component';
import { DriverService, CreateDriverDTO } from '../../service/driver.service';

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

  newVehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    plate: '',
    seatNumber: 4,
    babySeat: false,
    petFriendly: false,
  };

  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading: boolean = false;

  constructor(private driverService: DriverService, private cd: ChangeDetectorRef) {}

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

    const createDriverDTO: CreateDriverDTO = {
      accountDTO: {
        email: this.newDriverData.email,
      },
      createUserDTO: {
        name: this.newDriverData.firstName,
        lastName: this.newDriverData.lastName,
        homeAddress: this.newDriverData.address,
        phone: this.newDriverData.phone,
        image: this.newDriverData.image || '',
      },
      vehicleDTO: {
        vehicleTypeDTO: this.mapVehicleTypeDTO(this.newVehicleData.type),
        model: this.newVehicleData.model,
        plate: this.newVehicleData.plate,
        seatNumber: this.newVehicleData.seatNumber,
        babySeat: this.newVehicleData.babySeat,
        petFriendly: this.newVehicleData.petFriendly,
      },
    };

    this.isLoading = true;
    this.driverService.registerDriver(createDriverDTO).subscribe({
      next: () => {
        this.showSuccess('Driver registered successfully!');

        setTimeout(() => {
          this.resetForms();
        }, 2000);
        this.cd.detectChanges();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error registering driver:', error);
        console.error('Error details:', error.error);

        let errorMsg = 'Failed to register driver. Please try again.';

        if (error.error?.message) {
          errorMsg = error.error.message;
        } else if (typeof error.error === 'string') {
          errorMsg = 'Mail already exists.';
        } else if (error.status === 409) {
          errorMsg = 'License plate already exists.';
        } else if (error.status === 400) {
          errorMsg = 'Invalid data provided. Please check all fields.';
        } else if (error.status === 500) {
          errorMsg = 'Server error. Please try again later.';
        } else if (error.status === 0) {
          errorMsg = 'Cannot connect to server. Please check if backend is running.';
        }

        this.showError(errorMsg);
        this.cd.detectChanges();
        this.isLoading = false;
      },
    });
  }

  private mapVehicleTypeDTO(type: string): { id: number | null; name: string; price: number } {
    const typeMap: { [key: string]: { id: number | null; name: string; price: number } } = {
      standard: { id: null, name: 'STANDARD', price: 0 },
      luxury: { id: null, name: 'LUXURY', price: 0 },
      van: { id: null, name: 'VAN', price: 0 },
    };
    return typeMap[type.toLowerCase()] || { id: null, name: 'STANDARD', price: 1.0 };
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
    this.cd.detectChanges();
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.errorMessage = null;
    setTimeout(() => {
      this.successMessage = null;
    }, 5000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.successMessage = null;
    setTimeout(() => {
      this.errorMessage = null;
    }, 5000);
  }
}
