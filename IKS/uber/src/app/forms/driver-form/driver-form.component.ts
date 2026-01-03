import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface DriverFormData {
  firstName: string;
  lastName: string;
  address: string;
  phone: string;
  email: string;
}

@Component({
  selector: 'app-driver-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './driver-form.component.html',
  styleUrls: ['./driver-form.component.css'],
})
export class DriverFormComponent {
  @Input() formData: DriverFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  @Input() originalData: DriverFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  @Input() buttonLabel: string = 'Save changes';
  @Input() emailReadonly: boolean = true;
  @Input() showProfilePicture: boolean = false;
  @Input() showButton: boolean = true;
  @Input() userProfileImage: string = 'accountpic.png';

  @Output() formSubmit = new EventEmitter<DriverFormData>();
  @Output() profileImageChange = new EventEmitter<string>();

  hasChanges(): boolean {
    return (
      this.formData.firstName !== this.originalData.firstName ||
      this.formData.lastName !== this.originalData.lastName ||
      this.formData.address !== this.originalData.address ||
      this.formData.phone !== this.originalData.phone
    );
  }

  onSubmit() {
    if (this.hasChanges()) {
      this.formSubmit.emit(this.formData);
    }
  }

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
}
