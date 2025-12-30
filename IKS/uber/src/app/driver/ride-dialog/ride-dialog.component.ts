import { Component, Inject, Signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatCheckbox } from '@angular/material/checkbox';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-ride-dialog',
  templateUrl: './ride-dialog.component.html',
  styleUrls: ['./ride-dialog.component.css'],
  standalone: true,
  imports: [
    CommonModule,           
    MatCheckbox
  ],
  providers: [DatePipe]
})
export class RideDialogComponent {
  protected user: User | null

  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride,
  ) {
    let logged = sessionStorage.getItem('loggedUser')
    if (logged != null){
      this.user = JSON.parse(logged) as User
      console.log(this.user)
    } else {
      this.user = null
    }
  }

}
