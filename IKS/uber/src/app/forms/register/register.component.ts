import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RegistrationData, AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);
  cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  userProfileImage: string = 'accountpic.png';
  errormsg: string | null = null;

  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('', Validators.required),
    image: new FormControl(''),
    name: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    phone: new FormControl('', Validators.required),
  });
  filename: String = 'Profile Picture (Optional)';

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.filename = file.name;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Image = e.target.result;
        this.userProfileImage = base64Image;
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    }
  }

  detectErrors() {
    this.errormsg = null;
    const formValue = this.registerForm.value;
    if (!formValue.email) {
      this.errormsg = 'Email is required!';
    } else if (this.registerForm.get('email')?.hasError('email')) {
      this.errormsg = 'Please enter a valid email address!';
    } else if (!formValue.password) {
      this.errormsg = 'Password is required!';
    } else if (this.registerForm.get('password')?.hasError('minLength')) {
      this.errormsg = 'Password must be at least 6 characters long!';
    } else if (!formValue.confirmPassword) {
      this.errormsg = 'Please confirm your password!';
    } else if (formValue.password !== formValue.confirmPassword) {
      this.errormsg = 'Passwords do not match!';
    } else if (!formValue.name) {
      this.errormsg = 'Name is required!';
    } else if (!formValue.lastName) {
      this.errormsg = 'Last name is required!';
    } else if (!formValue.address) {
      this.errormsg = 'Address is required!';
    } else if (!formValue.phone) {
      this.errormsg = 'Phone number is required!';
    }
  }

  register() {
    if (this.registerForm.invalid) {
      this.detectErrors();
      return;
    }

    const formValue = this.registerForm.value;

    const registrationData: RegistrationData = {
      email: formValue.email!,
      password: formValue.password!,
      type: 'PASSENGER',
      name: formValue.name!,
      lastName: formValue.lastName!,
      homeAddress: formValue.address!,
      phone: formValue.phone!,
      image: this.userProfileImage || '',
    };

    console.log(registrationData.image);

    this.authService.register(registrationData).subscribe({
      next: (res) => this.router.navigate(['/login']),
      error: (err) => {
        this.errormsg = 'Account with this email address already exists!';
        this.cdr.detectChanges();
      },
    });
  }
}
