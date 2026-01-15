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

  loginForm = new FormGroup({
    email: new FormControl<string>('', Validators.required),
    password: new FormControl<string>('', Validators.required),
  });

  login() {
    if (this.loginForm.valid) {
      let creds: LoginCreds = {
        email: this.loginForm.value.email as string,
        password: this.loginForm.value.password as string
      }
      this.authService.login(creds).subscribe(
        (res) => {
          if (res) this.router.navigate(["/home"])
          else alert("Incorrect Username or Password!")
        }
      )
    } else {
      alert("Invalid Login Form input!")
    }
  }
}
