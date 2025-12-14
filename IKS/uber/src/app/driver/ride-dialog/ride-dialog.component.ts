import { Component, Inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatCheckbox } from '@angular/material/checkbox';

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
  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride
  ) {}
}
