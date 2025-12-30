import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { LoginCreds, UserService } from '../../service/user.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  userService: UserService = inject(UserService)
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
      this.userService.getUser(creds).subscribe({
        next: (response) => {
          sessionStorage.setItem('loggedUser', JSON.stringify(response))
          this.router.navigate(['/home'])
        },
        error: (err) => console.log(err)
      })
      console.log(creds)
    } else {
      alert("Invalid Login Form input!")
    }
  }
}
