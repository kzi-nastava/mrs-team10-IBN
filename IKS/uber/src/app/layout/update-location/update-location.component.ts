import { Component, inject, ViewChild, AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MapComponent } from '../../maps/map-basic/map.component';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-update-location-dialog',
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
  private dialogRef = inject(MatDialogRef<UpdateLocationComponent>);

  ngAfterViewInit() {
    setTimeout(() => {
      if (this.mapComponent && this.mapComponent['map']) {
        this.mapComponent['map'].invalidateSize();
      }
    }, 100);
  }

  onLocationSelected(address: string) {
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
      .put('/api/drivers/me/update-location', this.address, this.getAuthHeaders())
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.dialogRef.close(true);
        },
        error: (err) => {
          console.error('Failed to update location', err);
          this.isLoading = false;
          this.dialogRef.close(false);
        },
      });
  }

  cancel() {
    this.dialogRef.close(false);
  }
}
