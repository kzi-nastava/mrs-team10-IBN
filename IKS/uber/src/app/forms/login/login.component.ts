import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginCreds, AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);
  errormsg: string | null = null;
  isLoading = false;

  loginForm = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email]),
    password: new FormControl<string>('', Validators.required),
  });

  login() {
    this.errormsg = null;

    if (!this.loginForm.value.email) {
      this.errormsg = 'Email is required!';
      return;
    }

    if (this.loginForm.get('email')?.hasError('email')) {
      this.errormsg = 'Incorrect email format!';
      return;
    }

    if (!this.loginForm.value.password) {
      this.errormsg = 'Password is required!';
      return;
    }

    if (!this.loginForm.valid) {
      this.errormsg = 'Please fill in all fields correctly!';
      return;
    }

    const creds: LoginCreds = {
      email: this.loginForm.value.email as string,
      password: this.loginForm.value.password as string,
    };

    this.isLoading = true;
    this.loginForm.disable();

    this.authService.login(creds).subscribe({
      next: (res) => {
        this.authService.save(res.body);
        this.isLoading = false;
        this.loginForm.enable();

        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.isLoading = false;
        this.loginForm.enable();
        this.errormsg = 'Incorrect Credentials!';
      },
    });
  }
}
