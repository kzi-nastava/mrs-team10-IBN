import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-verify-account',
  imports: [ReactiveFormsModule],
  templateUrl: './verify-form.component.html',
  styleUrl: './verify-form.component.css',
})
export class VerifyFormComponent {
  router: Router = inject(Router);
  verifyForm = new FormGroup({
    password: new FormControl('', Validators.minLength(6)),
    confirm: new FormControl('', Validators.minLength(6)),
  });
  authService: AuthService = inject(AuthService);
  route: ActivatedRoute = inject(ActivatedRoute);
  cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  validToken: Boolean | null = null;
  showError: Boolean = false;
  errormsg = '';

  ngOnInit() {
    this.authService.checkSetPasswordToken(this.route.snapshot.paramMap.get('id')!).subscribe({
      next: (res) => {
        this.validToken = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.validToken = false;
        this.cdr.detectChanges();
      },
    });
  }

  verify() {
    if (!this.verifyForm.valid) {
      this.errormsg = 'Password needs to be at least 6 characters long!';
      this.showError = true;
    } else if (this.verifyForm.value.password != this.verifyForm.value.confirm) {
      this.errormsg = 'Field values not equal! Check your input';
      this.showError = true;
    } else {
      this.authService
        .setPassword(this.route.snapshot.paramMap.get('id')!, this.verifyForm.value.password!)
        .subscribe({
          next: (res) => {
            localStorage.clear();
            this.router.navigate(['/login']);
          },
          error: (err) => {
            this.errormsg = 'Something went wrong!';
            this.showError = true;
            this.cdr.detectChanges();
          },
        });
    }
  }
}
