import { Component, inject, Output, EventEmitter } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-change-password',
  imports: [ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css',
})
export class ChangePasswordComponent {
  authService: AuthService = inject(AuthService);
  private http: HttpClient = inject(HttpClient);
  changePassForm = new FormGroup({
    oldPass: new FormControl('', Validators.minLength(6)),
    newPass: new FormControl('', Validators.minLength(6)),
    confirmPass: new FormControl('', Validators.minLength(6)),
  });
  errormsg = '';
  showError = false;
  isLoading = false;
  @Output() closeModal = new EventEmitter<void>();
  @Output() passwordChanged = new EventEmitter<string>();

  private getAuthHeaders() {
    const token = localStorage.getItem('auth_token');
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
      }),
    };
  }

  change() {
    this.showError = false;

    if (!this.changePassForm.valid) {
      this.errormsg = 'Password needs to be at least 6 characters long!';
      this.showError = true;
      return;
    }

    const oldPass = this.changePassForm.value.oldPass;
    const newPass = this.changePassForm.value.newPass;
    const confirmPass = this.changePassForm.value.confirmPass;

    if (newPass !== confirmPass) {
      this.errormsg = 'New passwords do not match!';
      this.showError = true;
      return;
    }

    if (oldPass === newPass) {
      this.errormsg = 'New password must be different from old password!';
      this.showError = true;
      return;
    }

    const payload = {
      oldPassword: oldPass,
      newPassword: newPass,
    };

    this.isLoading = true;
    this.changePassForm.disable();

    this.http
      .put<{
        message?: string;
      }>(`${environment.apiHost}/account/me/change-password`, payload, this.getAuthHeaders())
      .subscribe({
        next: (response) => {
          this.isLoading = false;

          if (response && response.message && response.message.trim() !== '') {
            this.errormsg = response.message;
            this.showError = true;
          } else {
            this.passwordChanged.emit('Password changed successfully!');
            this.onClose();
          }
          this.changePassForm.enable();
        },
        error: (err) => {
          this.isLoading = false;
          this.changePassForm.enable();

          if (err.status === 401) {
            this.errormsg = 'Unauthorized. Please login again.';
          } else {
            this.errormsg = 'Failed to change password. Please try again.';
          }

          this.showError = true;
        },
      });
  }

  onClose() {
    this.changePassForm.reset();
    this.showError = false;
    this.errormsg = '';
    this.closeModal.emit();
  }
}
