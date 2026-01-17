import { inject, Injectable, signal, Signal } from '@angular/core';
import { Ride } from '../model/ride-history.model';
import { User } from '../model/user.model';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { AuthGuard } from '../auth/auth-guard';
import { AuthService } from './auth.service';

export interface RideOrderResponseDTO {
  rideId: number;
  price: number;
  status: string;

  driverName: string;
  driverPhone: string;
  driverRating: number;

  vehicleModel: string;
  vehiclePlate: string;
  vehicleColor?: string;

  vehicleLocation: VehicleLocationDTO;

  estimatedPickupMinutes: number;
  estimatedPickupTime: string;
}

export interface VehicleLocationDTO {
  latitude: number;
  longitude: number;
  address?: string;
}

export interface CreateRideDTO {
  startAddress: string;
  destinationAddress: string;
  distance: number;
  stops: string[];
  passengerEmails: string[];
  vehicleType: string;
  babySeat: boolean;
  petFriendly: boolean;
  scheduled: string;
  price: number;
  estimatedDuration: number;
}

export interface PriceDTO {
  price: number;
}

export interface GetRideDTO {
  id?: number;
  startLocation: string;
  endLocation: string;
  price: number;
  status?: string;
}

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private apiUrl = 'http://localhost:8090/api';
  private authService = inject(AuthService);
  private role = this.authService.role();

  constructor(private http: HttpClient) {
    this.loadRides();
  }

  // private getAuthHeaders() {
  //   const token = localStorage.getItem('authToken');
  //   return {
  //     headers: new HttpHeaders({
  //       Authorization: `Bearer ${token}`,
  //     }),
  //   };
  // }

  private _rides = signal<Ride[]>([]);
  rides = this._rides.asReadonly();

  loadRides() {
    if (this.role == 'driver')
      this.http
        .get<Ride[]>(`${environment.apiHost}/rides/driver`)
        .subscribe((rides) => this._rides.set(rides));
    else if (this.role == 'passenger')
      this.http
        .get<Ride[]>(`${environment.apiHost}/rides/passenger`)
        .subscribe((rides) => this._rides.set(rides));
  }

  loadRideDetails(rideId: number): Observable<Ride> {
    return this.http.get<Ride>(`${environment.apiHost}/rides/${rideId}`);
  }

  calculatePrice(dto: CreateRideDTO): Observable<PriceDTO> {
    return this.http.post<PriceDTO>(`${this.apiUrl}/rides/calculate-price`, dto);
  }

  orderRide(dto: CreateRideDTO): Observable<RideOrderResponseDTO> {
    return this.http.post<RideOrderResponseDTO>(`${this.apiUrl}/rides`, dto);
  }
}
