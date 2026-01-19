import { Component, Signal, computed, inject } from '@angular/core';
import { Ride } from '../../model/ride-history.model';
import { RideService } from '../../service/ride-history.service';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { DatePipe } from '@angular/common';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-scheduled-rides',
  imports: [
    NavBarComponent,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatDialogModule,
    MatNativeDateModule,
    DatePipe,
    CommonModule,
  ],
  templateUrl: './scheduled-rides.component.html',
  styleUrl: './scheduled-rides.component.css',
})
export class ScheduledRidesComponent {
  protected scheduled_rides: Signal<Ride[]>;

  constructor(private rideService: RideService) {
    this.scheduled_rides = computed(() => this.rideService.scheduled_rides());
    console.log(this.scheduled_rides());
  }
}
