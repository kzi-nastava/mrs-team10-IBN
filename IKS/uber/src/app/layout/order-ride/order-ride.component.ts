import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { MapComponent } from '../../maps/map-home/map.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FavoritesPopupComponent } from '../favorites-popup/favorites-popup.component';
import { Location } from '../../model/location.model';
import {
  RideService,
  CreateRideDTO,
  PriceDTO,
  RideOrderResponseDTO,
} from '../../service/ride-history.service';

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

  selectedCar = 'STANDARD';

  isShareRideOpen = false;
  passengerEmails: string[] = [];

  currentLocations: any[] = [];
  showFavoritesPopup = false;

  totalPrice: number | null = null;
  isBabyTravel = false;
  isPetTravel = false;
  isCalculating = false;
  isOrdering = false;
  estimatedDistance: number = 0;
  estimatedDuration: number | null = null;

  successMessage: string | null = null;
  errorMessage: string | null = null;

  private lastRouteSignature = '';

  constructor(
    private router: Router,
    private rideService: RideService,
    private cd: ChangeDetectorRef,
  ) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.locations = navigation.extras.state['locations'] || [];
      this.estimatedTime = navigation.extras.state['estimatedTime'] || '';
      this.estimatedDistance = navigation.extras.state['estimatedDistance'] || 0.0;
    }
  }

  ngOnInit() {
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

    const now = new Date();
    this.rideDate = now.toISOString().split('T')[0];
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    this.rideTime = `${hours}:${minutes}`;
  }

  onRouteCalculated(event: { distance: number; duration: number }) {
    this.estimatedDistance = event.distance;
    this.estimatedDuration = event.duration;

    if (this.estimatedDistance > 0 && !this.isCalculating) {
      this.calculatePrice();
    }
  }

  calculatePrice() {
    if (!this.fromLocation || !this.toLocation) {
      this.showError('Please select pickup and destination locations');
      return;
    }

    if (this.estimatedDistance === 0) {
      this.showError('Please wait for route calculation to complete');
      return;
    }

    this.isCalculating = true;

    let scheduledDateTime: string | null = null;
    if (this.timeOption === 'scheduled' && this.rideDate && this.rideTime) {
      scheduledDateTime = `${this.rideDate} ${this.rideTime}:00`;
    }

    const dto: CreateRideDTO = {
      startAddress: this.fromLocation,
      destinationAddress: this.toLocation,
      distance: this.estimatedDistance,
      stops: this.stops.filter((s) => s.trim() !== ''),
      passengerEmails: this.passengerEmails.filter((e) => e.trim() !== ''),
      vehicleType: this.selectedCar,
      babySeat: this.isBabyTravel,
      petFriendly: this.isPetTravel,
      scheduled: scheduledDateTime || '',
      price: 0,
      estimatedDuration: this.estimatedDuration || 30,
    };

    this.rideService.calculatePrice(dto).subscribe({
      next: (result: PriceDTO) => {
        this.totalPrice = Math.round(result.price);
        this.isCalculating = false;
        this.cd.detectChanges();
      },
      error: (error) => {
        console.error('Error calculating price:', error);
        let errorMsg = 'Failed to calculate price. ';

        if (error.error?.message) {
          errorMsg += error.error.message;
        } else if (error.message) {
          errorMsg += error.message;
        } else {
          errorMsg += 'Please check your input and try again.';
        }

        this.showError(errorMsg);
        this.isCalculating = false;
      },
    });
  }

  orderRide() {
    if (!this.totalPrice) {
      this.showError('Please calculate price first');
      return;
    }

    if (this.estimatedDistance === 0) {
      this.showError('Invalid route distance');
      return;
    }

    this.isOrdering = true;

    let scheduledDateTime: string | null = null;
    if (this.timeOption === 'scheduled' && this.rideDate && this.rideTime) {
      scheduledDateTime = `${this.rideDate} ${this.rideTime}:00`;
    }

    const dto: CreateRideDTO = {
      startAddress: this.fromLocation,
      destinationAddress: this.toLocation,
      distance: this.estimatedDistance,
      stops: this.stops.filter((s) => s.trim() !== ''),
      passengerEmails: this.passengerEmails.filter((e) => e.trim() !== ''),
      vehicleType: this.selectedCar,
      babySeat: this.isBabyTravel,
      petFriendly: this.isPetTravel,
      scheduled: scheduledDateTime || '',
      price: this.totalPrice,
      estimatedDuration: this.estimatedDuration || 30,
    };

    this.rideService.orderRide(dto).subscribe({
      next: (response: RideOrderResponseDTO) => {
        this.isOrdering = false;
        if (response !== null) {
          const pickupInfo =
            response.estimatedPickupMinutes != -1
              ? `Arrives in: ${response.estimatedPickupMinutes} min (${response.estimatedPickupTime})`
              : `Scheduled for: ${response.estimatedPickupTime}`;

          const successMsg = `
🚗 Ride Ordered Successfully!

💰 Price: ${response.price} RSD
👤 Driver: ${response.driverName}
📞 Phone: ${response.driverPhone}
🚙 Vehicle: ${response.vehicleModel} (${response.vehiclePlate})
⏱️ ${pickupInfo}
`.trim();

          this.showSuccess(successMsg);
        } else {
          let errorMsg = 'No available drivers. ';
          this.showError(errorMsg);
          return;
        }
      },
      error: (error) => {
        console.error('Error ordering ride:', error);
        this.isOrdering = false;

        let errorMsg = 'Failed to order ride. ';

        if (error.status === 204) {
          errorMsg = 'No available drivers at the moment. Please try again later.';
        } else if (error.error?.message) {
          errorMsg += error.error.message;
        } else {
          errorMsg += 'Please try again.';
        }

        this.showError(errorMsg);
      },
    });
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
    this.calculatePrice();
  }

  selectCar(carType: string) {
    this.selectedCar = carType;
    this.calculatePrice();
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
    const newLocationParts = [];
    if (this.fromLocation) newLocationParts.push(this.fromLocation);
    const validStops = this.stops.filter((s) => s.trim() !== '');
    if (validStops.length > 0) newLocationParts.push(...validStops);
    if (this.toLocation) newLocationParts.push(this.toLocation);

    const newLocationText = newLocationParts.join(' → ');
    const locationsChanged = newLocationText !== this.locationText;

    this.locationText = newLocationText;

    if (this.timeOption === 'now') {
      this.timeText = 'Leave now';
    } else {
      this.timeText = `${this.rideDate} at ${this.rideTime}`;
    }

    this.isDropdownOpen = false;

    if (locationsChanged) {
      this.totalPrice = null;
      this.estimatedDistance = 0;
      this.updateMapLocations();
      this.calculatePrice();
    }
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

    const newLocationsString = JSON.stringify(locations);
    const oldLocationsString = JSON.stringify(this.currentLocations);

    if (newLocationsString !== oldLocationsString) {
      this.currentLocations = [...locations];
    }
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

    this.calculatePrice();
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

    this.calculatePrice();
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

  showSuccess(message: string) {
    this.successMessage = message;
    this.errorMessage = null;
    this.cd.detectChanges();
    setTimeout(() => {
      this.successMessage = null;
      this.cd.detectChanges();
    }, 8000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.successMessage = null;
    this.cd.detectChanges();
    setTimeout(() => {
      this.errorMessage = null;
      this.cd.detectChanges();
    }, 5000);
  }
}
