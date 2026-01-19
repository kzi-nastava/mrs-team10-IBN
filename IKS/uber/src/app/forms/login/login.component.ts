import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { LoginCreds, AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  authService: AuthService = inject(AuthService)
  router: Router = inject(Router)
  errormsg: string | null = null;

  loginForm = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email]),
    password: new FormControl<string>('', Validators.required),
  });

  login() {
    if (this.loginForm.valid) {
      let creds: LoginCreds = {
        email: this.loginForm.value.email as string,
        password: this.loginForm.value.password as string
      }
      this.authService.login(creds).subscribe({
        next: (res) => this.authService.save(res.body),
        error: (err) => this.errormsg = "Incorrect Credentials!"
      })
    } else if (!this.loginForm.value.email) {
      this.errormsg = "Email is required!";
    } else if (this.loginForm.get('email')?.hasError('email')) {
      this.errormsg = "Incorrect email format!";
    } else if (!this.loginForm.value.password) {
      this.errormsg = "Password is required!";
    }
  }
}
