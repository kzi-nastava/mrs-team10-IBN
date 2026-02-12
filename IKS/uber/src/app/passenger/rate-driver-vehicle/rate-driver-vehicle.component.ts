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
import { Optional } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-rate-driver-vehicle',
  imports: [MatIconModule, CommonModule, RouterModule, FormsModule],
  templateUrl: './rate-driver-vehicle.component.html',
  styleUrls: ['./rate-driver-vehicle.component.css'],
})
export class RateDriverVehicleComponent {
  public review: Review = {
    id: 0,
    rideId: 0,
  };

  constructor(
    private reviewService: ReviewService,
    @Optional() private dialogRef: MatDialogRef<RateDriverVehicleComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: { rideId: number } | null,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
  ) {
    if (this.data?.rideId) {
      this.review.rideId = this.data.rideId;
    } else {
      const rideIdFromRoute = Number(this.route.snapshot.paramMap.get('rideId'));
      this.review.rideId = rideIdFromRoute;
    }
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

  postReview() {
    this.reviewService.postReview(this.review).subscribe({
      next: () => {
        this.dialog.open(SimpleMessageDialogComponent, {
          width: '300px',
          data: { message: 'Your review is submitted.' },
        });
      },
      error: () => {
        this.dialog.open(SimpleMessageDialogComponent, {
          width: '300px',
          data: { message: 'You cannot place review for this ride!' },
        });
      },
    });
  }
}
