import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { MapComponent } from '../../map/map.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-ride',
  imports: [RouterModule, NavBarComponent, MapComponent, FormsModule, CommonModule],
  templateUrl: './order-ride.component.html',
  styleUrl: './order-ride.component.css',
})
export class OrderRideComponent implements OnInit {
  isDropdownOpen = false;
  locationText = 'Kopernikova 23 → Železnička stanica';
  timeText = 'Leave now';

  fromLocation = 'Kopernikova 23';
  toLocation = 'Železnička stanica';
  stops: string[] = [];

  timeOption = 'now';
  rideDate = '';
  rideTime = '';

  selectedCar = 'standard';

  isShareRideOpen = false;
  passengerEmails: string[] = [];

  currentLocations: any[] = [];

  ngOnInit() {
    this.updateMapLocations();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  addStop() {
    this.stops.push('');
  }

  removeStop(index: number) {
    this.stops.splice(index, 1);
  }

  selectCar(carType: string) {
    this.selectedCar = carType;
  }

  toggleShareRide() {
    this.isShareRideOpen = !this.isShareRideOpen;
  }

  addPassenger() {
    this.passengerEmails.push('');
  }

  removePassenger(index: number) {
    this.passengerEmails.splice(index, 1);
  }

  getPassengerNames(): string {
    if (this.passengerEmails.length === 0) return '';

    const names = this.passengerEmails.map((email) => {
      const name = email.split('@')[0];
      return name.length > 10 ? name.substring(0, 10) + '...' : name;
    });

    if (names.length <= 2) {
      return names.join(', ');
    } else {
      return `${names[0]}, ${names[1]} +${names.length - 2} more`;
    }
  }

  confirmShareRide() {
    this.passengerEmails = this.passengerEmails.filter((email) => email.trim() !== '');

    if (this.passengerEmails.length > 0) {
    }

    this.isShareRideOpen = false;
  }

  confirmSelection() {
    let locationParts = [this.fromLocation];
    if (this.stops.length > 0) {
      locationParts = [...locationParts, ...this.stops.filter((s) => s.trim() !== '')];
    }
    locationParts.push(this.toLocation);
    this.locationText = locationParts.join(' → ');

    if (this.timeOption === 'now') {
      this.timeText = 'Leave now';
    } else {
      this.timeText = `${this.rideDate} at ${this.rideTime}`;
    }

    this.isDropdownOpen = false;

    this.updateMapLocations();
  }

  updateMapLocations() {
    const locations = [];

    if (this.fromLocation) {
      locations.push({ address: this.fromLocation, type: 'pickup' });
    }

    this.stops.forEach((stop, index) => {
      if (stop.trim() !== '') {
        locations.push({ address: stop, type: 'stop', index: index + 1 });
      }
    });

    if (this.toLocation) {
      locations.push({ address: this.toLocation, type: 'destination' });
    }

    this.currentLocations = [...locations];
  }

  getLocations() {
    return this.currentLocations;
  }
}
