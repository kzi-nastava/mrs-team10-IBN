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
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { RideCancellation, RouteService } from '../../service/route.service';
import { ReviewsComponent } from '../reviews/reviews.component';
import { ComplaintsComponent } from '../complaints/complaints.component';
import { Router } from '@angular/router';

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
  errorMessage: string | null = null;
  isFavorite$ = this.isFavoriteSubject.asObservable();

  constructor(
    public dialogRef: MatDialogRef<RideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: Ride,
    private dialog: MatDialog,
    private authService: AuthService,
    private rideService: RideService,
    private routeService: RouteService,
    private cdr: ChangeDetectorRef,
    private router: Router
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

  canBeCancelled() {
    const TEN_MINUTES = 10 * 60 * 1000;
    const now = Date.now();
    return new Date(this.ride.startTime).getTime() - now > TEN_MINUTES && !this.ride.canceled;
  }

  cancelRide() {
    const cancelled: RideCancellation = {
      id: this.ride.id,
      cancellationReason: 'Cancelled by passenger',
      cancelledByDriver: false,
    };
    this.routeService.cancelRide(cancelled).subscribe({
      next: (res) => {
        this.ride.price = 0;
        this.ride.canceled = true;
        this.cdr.detectChanges();
      },
    });
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

  viewReviews() {
    this.rideService.getReviews(this.ride.id).subscribe({
      next: (res) => {
        this.dialog.open(ReviewsComponent, {
          width: '70vw',
          data: { reviews: res },
        });
      },
    });
  }

  viewComplaints() {
    this.rideService.getComplaints(this.ride.id).subscribe({
      next: (res) => {
        this.dialog.open(ComplaintsComponent, {
          width: '70vw',
          data: { complaints: res },
        });
      },
    });
  }

  async goToOrder() {
    try {
      const ongoing = await firstValueFrom(this.rideService.hasOngoingRide());
      const numStations = this.ride.route.stations.length;
      if (!ongoing) {
        this.dialogRef.close();
        this.router.navigate(['/order-ride'], {
          state: {
            locations: this.ride.route.stations.map((station, index) => ({
              address: station.address
                .replace(/,?\sNovi Sad\b/gi, '')
                .replace(/,?\sSerbia\b/gi, '')
                .trim(),
              lat: station.lat,
              lon: station.lon,
              type: index === 0 ? 'pickup' : index === numStations - 1 ? 'destination' : 'stop',
              index,
            })),
          },
        });
      } else {
        this.showError('Please finish your ongoing ride before starting a new one.');
      }
    } catch (err) {
      console.error('Failed to check ongoing ride', err);
      this.showError('Could not verify ongoing ride. Please try again.');
    }
  }

  public showError(message: string) {
    this.errorMessage = message;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.errorMessage = null;
      this.cdr.detectChanges();
    }, 3000);
  }
}
