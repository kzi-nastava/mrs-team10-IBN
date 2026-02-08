import { ChangeDetectorRef, Component, OnInit, Signal, computed, inject } from '@angular/core';
import { Ride } from '../../model/ride-history.model';
import { RideService } from '../../service/ride-history.service';
import { AuthService } from '../../service/auth.service';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { RideDialogComponent } from '../ride-dialog/ride-dialog.component';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { DatePipe } from '@angular/common';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-ride-history',
  standalone: true,
  imports: [
    NavBarComponent,
    MatFormFieldModule,
    MatDatepickerModule,
    MatDialogModule,
    MatNativeDateModule,
    DatePipe,
    CommonModule,
    MatInputModule
  ],
  templateUrl: 'ride-history.component.html',
  styleUrls: ['ride-history.component.css'],
})
export class RideHistoryComponent implements OnInit{
  protected rides: Signal<Ride[]>;
  protected fromDate: Date | null = null;
  protected toDate: Date | null = null;
  protected role: string | null;

  constructor(
    private rideService: RideService, 
    private dialog: MatDialog, 
    private authService: AuthService, 
    private router: Router
  ) {
    this.rides = this.rideService.rides;
    this.role = authService.role();
  }

  ngOnInit(): void {
    this.rideService.resetRides();
    this.rideService.loadRides(window.location.search);
  }

  onScroll(event: any) {
    const element = event.target as HTMLElement;
    const atBottom = element.scrollHeight - element.scrollTop <= element.clientHeight + 500;
    
    if (atBottom) {
      this.rideService.loadRides(window.location.search);
    }
  }

  onSelectChange(event: any){
    const criteria = event.target.value;
    
    this.router.navigate(["/ride-history"], {
      queryParams: { sort: criteria },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.rideService.resetRides();
      this.rideService.loadRides(window.location.search);
    });
  }

  onFromDateChange(event: any){
    const fromDate = event.target.value.toISOString();
    
    this.router.navigate(["/ride-history"], {
      queryParams: { startFrom: fromDate },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.rideService.resetRides();
      this.rideService.loadRides(window.location.search);
    });
  }

  onToDateChange(event: any){
    const toDate = event.target.value.toISOString();
    
    this.router.navigate(["/ride-history"], {
      queryParams: { startTo: toDate },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.rideService.resetRides();
      this.rideService.loadRides(window.location.search);
    });
  }

  openRideDialog(ride: Ride) {
    this.rideService.loadRideDetails(ride.id).subscribe({
      next: (rideDetails: Ride) => {
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