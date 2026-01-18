import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-verify-account',
  imports: [],
  templateUrl: './verify-account.component.html',
  styleUrl: './verify-account.component.css',
})
export class VerifyAccountComponent {
  verificationHeader: string = "";
  verificationMessage: string = "";
  authService: AuthService = inject(AuthService);
  route: ActivatedRoute = inject(ActivatedRoute);
  cdr: ChangeDetectorRef = inject(ChangeDetectorRef)

  ngOnInit(){
    this.authService.verify(this.route.snapshot.paramMap.get("id")!).subscribe({
      next: (res) => {
        this.verificationHeader = "Account Successfully Verified"
        this.verificationMessage = "You can login with your account now!"
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.verificationHeader = "Invalid Verification Link!"
        this.verificationMessage = "Verification link has expired or has been already used!"
        this.cdr.detectChanges();
      }
    })
  }
}
