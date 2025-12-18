import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-verify-account',
  imports: [ReactiveFormsModule],
  templateUrl: './verify-account.component.html',
  styleUrl: './verify-account.component.css',
})
export class VerifyAccountComponent {
  router: Router = inject(Router);
  verifyForm = new FormGroup({
    password: new FormControl("", Validators.minLength(6)),
    confirm: new FormControl("", Validators.minLength(6))
  })

  verify(){
    if (!this.verifyForm.valid){
      alert("Password needs to be at least 6 characters long!");
    }
    else if (this.verifyForm.value.password != this.verifyForm.value.confirm){
      alert("Field values not equal! Check your input");
    } else {
      this.router.navigate(["/home"]);
    }
  }
}
