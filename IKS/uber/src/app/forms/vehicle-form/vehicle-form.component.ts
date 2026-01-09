import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface VehicleFormData {
  model: string;
  type: 'standard' | 'luxury' | 'van';
  licensePlate: string;
  seats: number;
  babyTransport: boolean;
  petTransport: boolean;
}

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './vehicle-form.component.html',
  styleUrls: ['./vehicle-form.component.css'],
})
export class VehicleFormComponent {
  @Input() vehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    licensePlate: '',
    seats: 4,
    babyTransport: false,
    petTransport: false,
  };
  @Input() originalData: VehicleFormData = {
    model: '',
    type: 'standard',
    licensePlate: '',
    seats: 4,
    babyTransport: false,
    petTransport: false,
  };

  @Input() buttonLabel: string = 'Send Changes';
  @Input() showAsModal: boolean = true;

  @Output() formSubmit = new EventEmitter<VehicleFormData>();
  @Output() closeModal = new EventEmitter<void>();

  onSubmit() {
    this.formSubmit.emit(this.vehicleData);
  }

  onClose() {
    this.closeModal.emit();
  }

  stopPropagation(event: Event) {
    event.stopPropagation();
  }

  isUnchanged(): boolean {
    return JSON.stringify(this.vehicleData) === JSON.stringify(this.originalData);
  }

  fieldTouched = false;

  markFieldTouched() {
    this.fieldTouched = true;
  }

  isModelInvalid(): boolean {
    return this.fieldTouched && !this.vehicleData.model?.trim();
  }

  isPlateInvalid(): boolean {
    return this.fieldTouched && !this.vehicleData.licensePlate?.trim();
  }

  isSeatsInvalid(): boolean {
    return (
      this.fieldTouched &&
      (this.vehicleData.seats == null || this.vehicleData.seats < 1 || this.vehicleData.seats > 9)
    );
  }

  isTypeInvalid(): boolean {
    return this.fieldTouched && !this.vehicleData.type;
  }

  isFormValid(): boolean {
    return (
      !!this.vehicleData.model?.trim() &&
      !!this.vehicleData.licensePlate?.trim() &&
      this.vehicleData.seats >= 1 &&
      this.vehicleData.seats <= 9 &&
      !!this.vehicleData.type
    );
  }
}
