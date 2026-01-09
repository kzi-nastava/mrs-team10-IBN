import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface UserFormData {
  firstName: string;
  lastName: string;
  address: string;
  phone: string;
  email: string;
  image?: string;
}

@Component({
  selector: 'app-user-form',
  imports: [FormsModule, CommonModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
  standalone: true,
})
export class UserFormComponent implements OnChanges {
  @Input() formData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
    image: '',
  };

  @Input() originalData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
    image: '',
  };

  @Input() buttonLabel: string = 'Save Changes';
  @Input() emailReadonly: boolean = false;
  @Input() userProfileImage: string = 'accountpic.png';
  @Input() showButton: boolean = true;

  @Input() isDriver: boolean = false;
  @Input() isDriverActive: boolean = false;
  @Input() onToggleStatus?: () => void;

  @Output() formSubmit = new EventEmitter<UserFormData>();
  @Output() profileImageChange = new EventEmitter<string>();

  formTouched = false;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['formData'] && !changes['formData'].firstChange) {
      this.formTouched = false;
    }
  }

  hasChanges(): boolean {
    if (this.formData)
      return (
        this.formData.firstName !== this.originalData.firstName ||
        this.formData.lastName !== this.originalData.lastName ||
        this.formData.address !== this.originalData.address ||
        this.formData.phone !== this.originalData.phone ||
        this.formData.email !== this.originalData.email
      );
    return true;
  }

  onSubmit() {
    this.formTouched = true;

    if (!this.isFormValid()) {
      console.warn('Form is invalid:', this.getValidationErrors());
      return;
    }

    this.formSubmit.emit(this.formData);
  }

  isFormValid(): boolean {
    if (this.formData) {
      if (!this.formData.firstName || this.formData.firstName.trim().length < 2) {
        return false;
      }

      if (!this.formData.lastName || this.formData.lastName.trim().length < 2) {
        return false;
      }

      if (!this.formData.address || this.formData.address.trim().length === 0) {
        return false;
      }

      const phonePattern = /^\d{9,15}$/;
      if (!this.formData.phone || !phonePattern.test(this.formData.phone)) {
        return false;
      }

      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.formData.email || !emailPattern.test(this.formData.email)) {
        return false;
      }
    }

    return true;
  }

  getValidationErrors(): string[] {
    const errors: string[] = [];

    if (this.formData) {
      if (!this.formData.firstName || this.formData.firstName.trim().length < 2) {
        errors.push('First name must be at least 2 characters');
      }

      if (!this.formData.lastName || this.formData.lastName.trim().length < 2) {
        errors.push('Last name must be at least 2 characters');
      }

      if (!this.formData.address || this.formData.address.trim().length === 0) {
        errors.push('Address is required');
      }

      const phonePattern = /^\d{9,15}$/;
      if (!this.formData.phone || !phonePattern.test(this.formData.phone)) {
        errors.push('Phone must be 9-15 digits');
      }

      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.formData.email || !emailPattern.test(this.formData.email)) {
        errors.push('Valid email is required');
      }
      return errors;
    }
    return [];
  }

  isFirstNameInvalid(): boolean {
    if (this.formData)
      return (
        this.formTouched && (!this.formData.firstName || this.formData.firstName.trim().length < 2)
      );
    return false;
  }

  isLastNameInvalid(): boolean {
    if (this.formData)
      return (
        this.formTouched && (!this.formData.lastName || this.formData.lastName.trim().length < 2)
      );
    return false;
  }

  isAddressInvalid(): boolean {
    if (this.formData)
      return (
        this.formTouched && (!this.formData.address || this.formData.address.trim().length === 0)
      );
    return false;
  }

  isPhoneInvalid(): boolean {
    if (this.formData) {
      const phonePattern = /^\d{9,15}$/;
      return this.formTouched && (!this.formData.phone || !phonePattern.test(this.formData.phone));
    }
    return false;
  }

  isEmailInvalid(): boolean {
    if (this.formData) {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return this.formTouched && (!this.formData.email || !emailPattern.test(this.formData.email));
    }
    return false;
  }

  getStatusColor(): string {
    return this.isDriverActive ? '#10b981' : '#ef4444';
  }

  getStatusText(): string {
    return this.isDriverActive ? 'Online' : 'Offline';
  }

  goOnline() {
    this.isDriverActive = !this.isDriverActive;
    this.onToggleStatus?.();
  }

  markFieldTouched() {
    this.formTouched = true;
  }
}
