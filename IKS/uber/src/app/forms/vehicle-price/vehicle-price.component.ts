import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface VehiclePriceData {
  standard: number;
  luxury: number;
  van: number;
}

@Component({
  selector: 'app-vehicle-price',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './vehicle-price.component.html',
  styleUrls: ['./vehicle-price.component.css'],
})
export class VehiclePriceComponent {

  prices: VehiclePriceData = {
    standard: 320,
    luxury: 440,
    van: 400,
  };

  @Output() closeModal = new EventEmitter<void>();
  @Output() savePrices = new EventEmitter<VehiclePriceData>();

  close() {
    this.closeModal.emit();
  }

  save() {
    this.savePrices.emit(this.prices);
  }

  stopPropagation(event: Event) {
    event.stopPropagation();
  }
}
