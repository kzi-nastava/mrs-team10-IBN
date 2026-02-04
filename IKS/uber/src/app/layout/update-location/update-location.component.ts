import {
  Component,
  inject,
  ViewChild,
  AfterViewInit,
  ChangeDetectorRef,
  OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MapComponent } from '../../maps/map-basic/map.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-update-location',
  standalone: true,
  imports: [FormsModule, MapComponent],
  templateUrl: './update-location.component.html',
  styleUrls: ['./update-location.component.css'],
})
export class UpdateLocationComponent implements AfterViewInit, OnInit {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  address: string = '';
  isLoading: boolean = false;
  private http = inject(HttpClient);
  isDriverActive: boolean = false;
  successMessage: string | null = '';
  errorMessage: string | null = '';
  private cd = inject(ChangeDetectorRef);

  ngOnInit() {
    this.loadDriverStatus();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (this.mapComponent && this.mapComponent['map']) {
        this.mapComponent['map'].invalidateSize();
      }
    }, 100);
  }

  onLocationSelected(address: string) {
    if (!address.toLowerCase().includes('novi sad')) {
      address = address + ', Novi Sad, Serbia';
    }
    this.address = address;
  }

  private getAuthHeaders() {
    const token = localStorage.getItem('auth_token');
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
      }),
    };
  }

  loadDriverStatus() {
    this.http
      .get<{ isActive: boolean }>(`${environment.apiHost}/drivers/me/status`, this.getAuthHeaders())
      .subscribe({
        next: (response) => {
          this.isDriverActive = response.isActive;
          this.cd.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load driver status', err);
        },
      });
  }

  updateLocation() {
    if (!this.address) return;

    this.isLoading = true;
    this.http
      .put(`${environment.apiHost}/drivers/me/update-location`, this.address, this.getAuthHeaders())
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.showSuccess('Location updated successfully!');
          this.mapComponent.clearAll();
        },
        error: (err) => {
          console.error('Failed to update location', err);
          this.isLoading = false;
          this.showError('Failed to update location');
          this.mapComponent.clearAll();
        },
      });
  }

  getStatusColor(): string {
    return this.isDriverActive ? '#10b981' : '#ef4444';
  }

  getStatusText(): string {
    return this.isDriverActive ? 'ONLINE' : 'OFFLINE';
  }

  goOnline() {
    const newStatus = !this.isDriverActive;

    this.http
      .put(
        `${environment.apiHost}/drivers/me/toggle-status?active=${newStatus}`,
        null,
        this.getAuthHeaders(),
      )
      .subscribe({
        next: () => {
          this.isDriverActive = newStatus;
          this.showSuccess(
            newStatus ? 'You are now ONLINE and available for rides' : 'You are now OFFLINE',
          );
        },
        error: (err) => {
          console.error('Failed to toggle status', err);
          if (err.error?.error) {
            this.showError(err.error.error);
          } else {
            this.showError('Failed to update status');
          }
        },
      });
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.cd.detectChanges();
    setTimeout(() => {
      this.successMessage = null;
      this.cd.detectChanges();
    }, 3000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.cd.detectChanges();
    setTimeout(() => {
      this.errorMessage = null;
      this.cd.detectChanges();
    }, 3000);
  }
}
