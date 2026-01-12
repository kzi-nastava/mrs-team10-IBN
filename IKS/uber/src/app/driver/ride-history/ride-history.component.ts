import { Component, Signal, computed, inject } from '@angular/core';
import { Ride } from '../../model/ride-history.model';
import { RideService } from '../../service/ride-history.service';
import { AuthService } from '../../service/auth.service';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { RideDialogComponent } from '../ride-dialog/ride-dialog.component';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { DatePipe } from '@angular/common';
import { User } from '../../model/user.model';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

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
    DatePipe,
    CommonModule
  ],
  templateUrl: 'ride-history.component.html',
  styleUrls: ['ride-history.component.css'],
})
export class RideHistoryComponent {
  protected rides: Signal<Ride[]>;
  protected user: User | null;
  protected fromDate: Date | null = null;
  protected toDate: Date | null = null;

  constructor(private rideService: RideService, private dialog: MatDialog) {
    this.rides = computed(() => this.rideService.rides());
    let logged = sessionStorage.getItem('loggedUser')
    if (logged != null){
      this.user = JSON.parse(logged) as User
    } else {
      this.user = null
    }
  }

  onSelectChange(event: any){
    this.rides = computed(() => {
      const rides = this.rideService.rides();
      const criteria = event.target.value
      switch(criteria) {
        case 'price-asc': return [...rides].sort((a, b) => a.price - b.price);
        case 'price-desc': return [...rides].sort((a, b) => b.price - a.price);
        case 'start-asc': return [...rides].sort((a, b) => a.startTime.getTime() - b.startTime.getTime());
        case 'start-desc': return [...rides].sort((a, b) => b.startTime.getTime() - a.startTime.getTime());
        case 'end-asc': return [...rides].sort((a, b) => a.endTime.getTime() - b.endTime.getTime());
        case 'end-desc': return [...rides].sort((a, b) => b.endTime.getTime() - a.endTime.getTime());
      }
      return rides;
    });
  }

  onFromDateChange(event: any){
    this.fromDate = event.target.value
    this.rides = computed(() => {
      const rides = this.rideService.rides();
      console.log(this.fromDate)
      console.log(this.toDate)
      if (this.toDate === null){
        return [...rides].filter((ride) => ride.startTime.getTime() >= this.fromDate!.getTime())
      }
      return [...rides].filter((ride) => ride.startTime.getTime() >= this.fromDate!.getTime() && ride.startTime.getTime() <= this.toDate!.getTime())
    });
  }

  onToDateChange(event: any){
    this.toDate = event.target.value
    this.rides = computed(() => {
      const rides = this.rideService.rides();
      console.log(this.fromDate)
      console.log(this.toDate)
      if (this.fromDate === null){
        return [...rides].filter((ride) => ride.startTime.getTime() <= this.toDate!.getTime())
      }
      return [...rides].filter((ride) => ride.startTime.getTime() >= this.fromDate!.getTime() && ride.startTime.getTime() <= this.toDate!.getTime())
    });
  }

  openRideDialog(ride: Ride) {
    this.rideService.loadRideDetails(ride.id).subscribe({
      next: (rideDetails: Ride) => {
        console.log(rideDetails)
        this.dialog.open(RideDialogComponent, {
          width: '50vw',
          height: 'auto',
          maxWidth: '80vw',
          data: rideDetails 
        });
      },
      error: (err : string) => console.error('Error loading ride details', err)
    });
  }
}
