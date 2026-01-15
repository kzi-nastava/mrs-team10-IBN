import { Component, Inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Review } from '../../model/review.model';
import { ReviewService } from '../../service/review.service';
import { User } from '../../model/user.model';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatDialogRef } from '@angular/material/dialog';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-rate-driver-vehicle',
  imports: [MatIconModule, CommonModule, RouterModule, FormsModule],
  templateUrl: './rate-driver-vehicle.component.html',
  styleUrls: ['./rate-driver-vehicle.component.css'],
})

export class RateDriverVehicleComponent {
  protected review: Review = {
        id: 0,
        rideId: 0,
  };

  constructor(private reviewService: ReviewService,
        private dialogRef: MatDialogRef<RateDriverVehicleComponent>,
        @Inject(MAT_DIALOG_DATA) public data: {rideId: number},
        private dialog: MatDialog
){
  this.review.rideId = data.rideId;
}
  stars = [1, 2, 3, 4, 5];
  driverHover = 0;
  vehicleHover = 0;

  rateDriver(value: number) {
    this.review.driverRating = value;
  }

  hoverDriver(value: number) {
    this.driverHover = value;
  }

  rateVehicle(value: number) {
    this.review.vehicleRating = value;
  }

  hoverVehicle(value: number) {
    this.vehicleHover = value;
  }

  postReview(){
    this.reviewService.postReview(this.review).subscribe({
    next: (res) => {
      this.dialog.open(SimpleMessageDialogComponent, {
      width: '300px',
      data: { message: "Your review is submitted." }
    });
    },
    error: (err) => {
      console.error('Error posting review', err);
    }
  });
  }
}



