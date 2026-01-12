import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { RegistrationData, AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router)

  registerForm = new FormGroup({
    email: new FormControl("", Validators.required),
    password: new FormControl("", Validators.required),
    confirmPassword: new FormControl("", Validators.required),
    image: new FormControl(""),
    name: new FormControl("", Validators.required),
    lastName: new FormControl("", Validators.required),
    address: new FormControl("", Validators.required),
    phone: new FormControl("", Validators.required)
  })
  filename: String = "Profile Picture (Optional)";

  onFileSelected(event: any) {
    this.filename = event.target.files[0].name;
  }

  register(){
    if (this.registerForm.invalid) {
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
      image: formValue.image || ''
    };

    this.authService.register(registrationData).subscribe(
      (res) => {
        if (res) this.router.navigate(["/home"])
        else alert("Account already exists!")
      }
    )
  }
}
