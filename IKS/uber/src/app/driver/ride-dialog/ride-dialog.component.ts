import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Ride } from '../../model/ride-history.model';
import { MatCheckbox } from '@angular/material/checkbox';
import { MapComponent } from '../../maps/map-basic/map.component';
import { MatDialogModule } from '@angular/material/dialog';
import { RateDriverVehicleComponent } from '../../passenger/rate-driver-vehicle/rate-driver-vehicle.component';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';
import { AuthService } from '../../service/auth.service';
import { RideService } from '../../service/ride-history.service';
import { BehaviorSubject } from 'rxjs';
import { RideCancellation, RouteService } from '../../service/route.service';

@Component({
  selector: 'app-ride-dialog',
  templateUrl: './ride-dialog.component.html',
  styleUrls: ['./ride-dialog.component.css'],
  standalone: true,
  imports: [CommonModule, MatCheckbox, MapComponent, MatDialogModule],
  providers: [DatePipe],
})
export class RideDialogComponent implements OnInit {
  protected role: string | null;
  private isFavoriteSubject = new BehaviorSubject<boolean>(false);
  isFavorite$ = this.isFavoriteSubject.asObservable();

  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride,
    private dialog: MatDialog,
    private authService: AuthService,
    private rideService: RideService,
    private routeService: RouteService,
    private cdr: ChangeDetectorRef
  ) {
    this.role = authService.role();
  }

  ngOnInit() {
    this.checkIfFavorite();
  }

  checkIfFavorite() {
    this.rideService.getFavoriteRoutes().subscribe({
      next: (favorites) => {
        const isFav = favorites.some((fav) => fav?.routeDTO?.id === this.ride.route?.id);
        this.isFavoriteSubject.next(isFav);
      },
      error: (err) => {
        console.error('Error checking favorites', err);
        this.isFavoriteSubject.next(false);
      },
    });
  }

  toggleFavorite() {
    if (!this.ride.route?.id) {
      console.error('Route ID not found');
      return;
    }

    const currentValue = this.isFavoriteSubject.value;

    if (currentValue) {
      this.rideService.removeFromOtherFavorites(this.ride.route.id).subscribe({
        next: () => {
          this.isFavoriteSubject.next(false);
          this.showMessage('Removed from favorites');
        },
        error: (err) => {
          console.error('Error removing favorite', err);
          this.showMessage('Failed to remove from favorites');
        },
      });
    } else {
      this.rideService.addToFavorites(this.ride.route.id).subscribe({
        next: () => {
          this.isFavoriteSubject.next(true);
          this.showMessage('Added to favorites');
        },
        error: (err) => {
          console.error('Error adding favorite', err);
          this.showMessage('Failed to add to favorites');
        },
      });
    }
  }

  canBeCancelled(){
    const TEN_MINUTES = 10 * 60 * 1000 
    const now = Date.now();
    console.log(this.ride.startTime)
    return new Date(this.ride.startTime).getTime() - now > TEN_MINUTES && !this.ride.canceled
  }

  cancelRide(){
    const cancelled: RideCancellation = {
      id: this.ride.id,
      cancellationReason: 'Cancelled by passenger',
      cancelledByDriver: false
    }
    console.log(cancelled)
    this.routeService.cancelRide(cancelled).subscribe({
      next: (res) => {
        this.ride.price = 0
        this.ride.canceled = true;
        this.cdr.detectChanges()
      }
    })
  }

  showMessage(message: string) {
    setTimeout(() => {
      this.dialog.open(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message },
      });
    }, 0);
  }

  openRateDialog() {
    const THREE_DAYS = 3 * 24 * 60 * 60 * 1000;
    const now = Date.now();

    if (now - new Date(this.ride.startTime).getTime() > THREE_DAYS) {
      this.dialog.open(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message: "You can't rate this drive because more than 3 days have passed." },
      });
      return;
    }

    this.dialog.open(RateDriverVehicleComponent, {
      width: '100vw',
      maxWidth: '600px',
      data: { rideId: this.ride.id },
    });
  }

  viewReviews(){}

  viewComplaints(){}
}
