import { Component, Inject, Signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatCheckbox } from '@angular/material/checkbox';
import { User } from '../../model/user.model';
import { MapComponent } from '../../map/map.component';
import { MatDialogModule } from '@angular/material/dialog';
import { RateDriverVehicleComponent } from '../../passenger/rate-driver-vehicle/rate-driver-vehicle.component';

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
  protected user: User | null

  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride,
    private dialog: MatDialog
  ) {
    let logged = sessionStorage.getItem('loggedUser')
    if (logged != null){
      this.user = JSON.parse(logged) as User
      console.log(this.user)
    } else {
      this.user = null
    }

  }

  openRateDialog() {
    this.dialog.open(RateDriverVehicleComponent, {
    width:'100vw',
    maxWidth:'600px',
    data: { rideId: this.ride.id }  

    });
  }    

}

