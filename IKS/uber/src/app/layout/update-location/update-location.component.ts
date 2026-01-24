import { Component, inject, ViewChild, AfterViewInit, ChangeDetectorRef } from '@angular/core';
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
export class UpdateLocationComponent implements AfterViewInit {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  address: string = '';
  isLoading: boolean = false;
  private http = inject(HttpClient);
  isDriverActive: boolean = false;
  successMessage: string | null = '';
  errorMessage: string | null = '';
  private cd = inject(ChangeDetectorRef);

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
    this.isDriverActive = !this.isDriverActive;
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
