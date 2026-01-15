import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { MapComponent } from '../../maps/map-basic/map.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FavoritesPopupComponent } from '../favorites-popup/favorites-popup.component';
import { Location } from '../../model/location.model';

@Component({
  selector: 'app-order-ride',
  imports: [
    RouterModule,
    NavBarComponent,
    MapComponent,
    FormsModule,
    CommonModule,
    FavoritesPopupComponent,
  ],
  templateUrl: './order-ride.component.html',
  styleUrl: './order-ride.component.css',
})
export class OrderRideComponent implements OnInit {
  locations: Location[] = [];
  estimatedTime: string = '';

  isDropdownOpen = false;
  locationText = '';
  timeText = 'Leave now';

  fromLocation = '';
  toLocation = '';
  stops: string[] = [];

  timeOption = 'now';
  rideDate = '';
  rideTime = '';

  selectedCar = 'standard';

  isShareRideOpen = false;
  passengerEmails: string[] = [];

  currentLocations: any[] = [];
  showFavoritesPopup = false;

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.locations = navigation.extras.state['locations'] || [];
      this.estimatedTime = navigation.extras.state['estimatedTime'] || '';
    }
  }

  ngOnInit() {
    console.log('Received locations:', this.locations);
    console.log('Estimated time:', this.estimatedTime);

    if (this.locations && this.locations.length > 0) {
      const pickup = this.locations.find((loc) => loc.type === 'pickup');
      const destination = this.locations.find((loc) => loc.type === 'destination');
      const stops = this.locations.filter((loc) => loc.type === 'stop');

      if (pickup) this.fromLocation = pickup.address;
      if (destination) this.toLocation = destination.address;
      this.stops = stops.map((s) => s.address);

      this.updateLocationText();
    }

    this.updateMapLocations();
  }

  openFavorites() {
    this.showFavoritesPopup = true;
  }

  onRouteSelected(route: any) {
    this.fromLocation = route.from;
    this.toLocation = route.to;
    this.stops = [...route.stops];
    this.confirmSelection();
    this.showFavoritesPopup = false;
  }

  closeFavorites() {
    this.showFavoritesPopup = false;
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

  onLocationAdded(address: string) {
    if (!this.fromLocation || this.fromLocation.trim() === '') {
      this.fromLocation = address;
    } else if (!this.toLocation || this.toLocation.trim() === '') {
      this.toLocation = address;
    } else {
      this.stops.push(this.toLocation);
      this.toLocation = address;
    }

    this.updateLocationText();
  }

  onLocationRemoved(index: number) {
    const totalLocations = 1 + this.stops.length + 1;

    if (index === 0) {
      if (this.stops.length > 0) {
        this.fromLocation = this.stops[0];
        this.stops.splice(0, 1);
      } else if (this.toLocation && this.toLocation.trim() !== '') {
        this.fromLocation = this.toLocation;
        this.toLocation = '';
      } else {
        this.fromLocation = '';
      }
    } else if (index === totalLocations - 1) {
      if (this.stops.length > 0) {
        this.toLocation = this.stops[this.stops.length - 1];
        this.stops.splice(this.stops.length - 1, 1);
      } else {
        this.toLocation = '';
      }
    } else if (index > 0 && index < totalLocations - 1) {
      const stopIndex = index - 1;
      this.stops.splice(stopIndex, 1);
    }

    this.updateLocationText();
  }

  private updateLocationText() {
    let locationParts = [];

    if (this.fromLocation && this.fromLocation.trim() !== '') {
      locationParts.push(this.fromLocation);
    }

    const validStops = this.stops.filter((s) => s && s.trim() !== '');
    if (validStops.length > 0) {
      locationParts = [...locationParts, ...validStops];
    }

    if (this.toLocation && this.toLocation.trim() !== '') {
      locationParts.push(this.toLocation);
    }

    if (locationParts.length > 0) {
      this.locationText = locationParts.join(' → ');
    } else {
      this.locationText = 'Select locations on map';
    }

    this.updateMapLocations();
  }
}
