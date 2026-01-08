import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface UserFormData {
  firstName: string;
  lastName: string;
  address: string;
  phone: string;
  email: string;
}

@Component({
  selector: 'app-user-form',
  imports: [FormsModule, CommonModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
})
export class UserFormComponent {
  @Input() formData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  @Input() originalData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  @Input() buttonLabel: string = 'Save Changes';
  @Input() emailReadonly: boolean = false;
  @Input() showProfilePicture: boolean = false;
  @Input() userProfileImage: string = 'accountpic.png';
  @Input() showButton: boolean = true;

  @Input() isDriver: boolean = false;
  @Input() isDriverActive: boolean = false;
  @Input() onToggleStatus?: () => void;

  @Output() formSubmit = new EventEmitter<UserFormData>();
  @Output() profileImageChange = new EventEmitter<string>();

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profileImageChange.emit(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  hasChanges(): boolean {
    return (
      this.formData.firstName !== this.originalData.firstName ||
      this.formData.lastName !== this.originalData.lastName ||
      this.formData.address !== this.originalData.address ||
      this.formData.phone !== this.originalData.phone ||
      this.formData.email !== this.originalData.email
    );
  }

  onSubmit() {
    this.formSubmit.emit(this.formData);
  }

  getStatusColor(): string {
    return this.isDriverActive ? '#10b981' : '#ef4444';
  }

  getStatusText(): string {
    return this.isDriverActive ? 'Online' : 'Offline';
  }

  goOnline() {
    this.isDriverActive = true;
  }
}
