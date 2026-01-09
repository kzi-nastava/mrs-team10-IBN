import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
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
export class VehicleFormComponent implements OnChanges {
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

  formTouched = false;
  touchedFields = {
    model: false,
    type: false,
    licensePlate: false,
    seats: false,
  };

  ngOnChanges(changes: SimpleChanges) {
    if (changes['vehicleData'] && !changes['vehicleData'].firstChange) {
      this.formTouched = false;
      this.touchedFields = {
        model: false,
        type: false,
        licensePlate: false,
        seats: false,
      };
    }
  }

  onSubmit() {
    this.formTouched = true;
    this.touchedFields = {
      model: true,
      type: true,
      licensePlate: true,
      seats: true,
    };

    if (!this.isFormValid()) {
      console.warn('Vehicle form is invalid');
      return;
    }

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

  markFieldTouched(field?: 'model' | 'type' | 'licensePlate' | 'seats') {
    this.formTouched = true;
    if (field) {
      this.touchedFields[field] = true;
    }
  }

  isModelInvalid(): boolean {
    return this.touchedFields.model && !this.vehicleData.model?.trim();
  }

  isPlateInvalid(): boolean {
    return this.touchedFields.licensePlate && !this.vehicleData.licensePlate?.trim();
  }

  isSeatsInvalid(): boolean {
    return (
      this.touchedFields.seats &&
      (this.vehicleData.seats == null || this.vehicleData.seats < 1 || this.vehicleData.seats > 9)
    );
  }

  isTypeInvalid(): boolean {
    return this.touchedFields.type && !this.vehicleData.type;
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
