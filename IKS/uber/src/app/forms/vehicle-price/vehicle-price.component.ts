import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { VehiclePriceData } from '../../model/vehicle-price.model';
import { signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';

@Component({
  selector: 'app-vehicle-price',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './vehicle-price.component.html',
  styleUrls: ['./vehicle-price.component.css'],
})
export class VehiclePriceComponent {
  public prices = signal<VehiclePriceData[]>([]);

  constructor(
    private http: HttpClient,
    private dialog: MatDialog,
  ) {
    this.loadVehiclePrices();
  }

  @Output() closeModal = new EventEmitter<void>();
  @Output() savePrices = new EventEmitter<VehiclePriceData>();

  loadVehiclePrices() {
    this.http
      .get<VehiclePriceData[]>(`${environment.apiHost}/prices`)
      .subscribe((prices) => this.prices.set(prices));
  }

  close() {
    this.closeModal.emit();
  }

  save() {
    this.http.put(`${environment.apiHost}/prices`, this.prices()).subscribe({
      next: () => {
        this.dialog.open(SimpleMessageDialogComponent, {
          width: '300px',
          data: { message: 'Prices are saved.' },
        });
      },
      error: (err) => {
        console.error('Error posting review', err);
      },
    });
  }

  stopPropagation(event: Event) {
    event.stopPropagation();
  }
}
