import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
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
}
