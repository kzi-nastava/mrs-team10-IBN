import { Component, Inject, Signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatCheckbox } from '@angular/material/checkbox';
import { User } from '../../model/user.model';
import { MapComponent } from '../../maps/map-basic/map.component';
import { MatDialogModule } from '@angular/material/dialog';
import { RateDriverVehicleComponent } from '../../passenger/rate-driver-vehicle/rate-driver-vehicle.component';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';
import { AuthService } from '../../service/auth.service';
@Component({
  selector: 'app-ride-dialog',
  templateUrl: './ride-dialog.component.html',
  styleUrls: ['./ride-dialog.component.css'],
  standalone: true,
  imports: [
    CommonModule,           
    MatCheckbox,
    MapComponent,
    MatDialogModule
  ],
  providers: [DatePipe]
})
export class RideDialogComponent {
  protected role: string | null;
  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride,
    private dialog: MatDialog,
    private authService: AuthService
  ) {
    this.role = authService.role();
  }

  openRateDialog() {
    const THREE_DAYS = 3 * 24 * 60 * 60 * 1000; 
    const now = Date.now();

    if (now - new Date(this.ride.startTime).getTime() > THREE_DAYS) {
      this.dialog.open(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message: "You can't rate this drive because more than 3 days have passed." }
      });
      return;
    }

    this.dialog.open(RateDriverVehicleComponent, {
      width: '100vw',
      maxWidth: '600px',
      data: { rideId: this.ride.id }
    });
  }


}

