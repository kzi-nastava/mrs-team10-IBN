import { Component, Signal } from '@angular/core';
import { Ride } from '../../model/ride-history.model';
import { RideService } from '../../service/ride-history.service';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { RideDialogComponent } from '../ride-dialog/ride-dialog.component';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-ride-history',
  standalone: true,
  imports: [
    NavBarComponent,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatDialogModule,
    MatNativeDateModule,
    DatePipe
  ],
  templateUrl: 'ride-history.component.html',
  styleUrls: ['ride-history.component.css'],
})
export class RideHistoryComponent {
  protected rides: Signal<Ride[]>;

  constructor(private service: RideService, private dialog: MatDialog) {
    this.rides = this.service.rides;
  }

  openRideDialog(ride: Ride) {
    this.dialog.open(RideDialogComponent, {
      width: '50vw',
      height: '82vh',
      maxWidth: '80vw',
      data: ride
    });
    console.log(ride);
  }
}
