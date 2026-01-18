import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  forgotPasswordForm = new FormGroup({
    email: new FormControl("", [Validators.required, Validators.email]),
  })
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);
  errormsg: string | null = null;
  cdr: ChangeDetectorRef = inject(ChangeDetectorRef)

  requestPasswordReset(){
    if(this.forgotPasswordForm.valid){
      this.authService.requestPasswordReset(this.forgotPasswordForm.value.email as string)
      .subscribe({
        next: (res) => {
          this.router.navigate(["/home"])
        },
        error:(err) => {
          this.errormsg = "Account with this email does not exist!"
          this.cdr.detectChanges()
        }
      })
    } else {
      this.errormsg = "Incorrect Email Format!"
    }
  }
}
