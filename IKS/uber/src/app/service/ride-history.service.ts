import { inject, Injectable, signal, Signal } from '@angular/core';
import { Ride } from '../model/ride-history.model';
import { User } from '../model/user.model';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { AuthGuard } from '../auth/auth-guard';
import { AuthService } from './auth.service';
import { PageResponse } from '../model/page-response.model';

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

export interface CoordinateDTO {
  id?: number | null;
  lat: number;
  lon: number;
  address: string;
}

export interface CreateRideDTO {
  startAddress: CoordinateDTO;
  destinationAddress: CoordinateDTO;
  distance: number;
  stops: CoordinateDTO[];
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
  private authService = inject(AuthService);
  private role = this.authService.role();

  ridesPage = 0;
  scheduledPage = 0;
  pageSize = 5;

  loadingRides = false;
  hasMoreRides = true;

  loadingScheduled = false;
  hasMoreScheduled = true;

  constructor(private http: HttpClient) {
  }

  private _rides = signal<Ride[]>([]);
  rides = this._rides.asReadonly();

  private _scheduled_rides = signal<Ride[]>([]);
  scheduled_rides = this._scheduled_rides.asReadonly();

  loadRides(query: string) {
    if (this.loadingRides || !this.hasMoreRides) return;
    this.loadingRides = true;

    const url = `${environment.apiHost}/rides/history${query}`;

    this.http
      .get<PageResponse<Ride>>(url, {
        params: {
          page: this.ridesPage,
          size: this.pageSize,
        },
      })
      .subscribe((res) => {
        const current = this._rides();
        this._rides.set([...current, ...res.content]);
        this.ridesPage++;
        this.hasMoreRides = this.ridesPage < res.totalPages;
        this.loadingRides = false;
      });
  }

  loadScheduledRides() {
    if (this.loadingScheduled || !this.hasMoreScheduled) return;

    this.loadingScheduled = true;

    this.http
      .get<PageResponse<Ride>>(`${environment.apiHost}/rides/scheduledRides`, {
        params: {
          page: this.scheduledPage,
          size: this.pageSize,
        },
      })
      .subscribe((res) => {
        const current = this._scheduled_rides();
        this._scheduled_rides.set([...current, ...res.content]);
        this.scheduledPage++;
        this.hasMoreScheduled = this.scheduledPage < res.totalPages;
        this.loadingScheduled = false;
      });
  }

  resetRides() {
    this._rides.set([]);
    this.ridesPage = 0;
    this.hasMoreRides = true;
    this.loadingRides = false;
  }

  resetScheduledRides() {
    this._scheduled_rides.set([]);
    this.scheduledPage = 0;
    this.hasMoreScheduled = true;
    this.loadingScheduled = false;
  }

  loadRideDetails(rideId: number): Observable<Ride> {
    return this.http.get<Ride>(`${environment.apiHost}/rides/${rideId}`);
  }

  calculatePrice(dto: CreateRideDTO): Observable<PriceDTO> {
    return this.http.post<PriceDTO>(`${environment.apiHost}/rides/calculate-price`, dto);
  }

  orderRide(dto: CreateRideDTO): Observable<RideOrderResponseDTO> {
    return this.http.post<RideOrderResponseDTO>(`${environment.apiHost}/rides`, dto);
  }

  getFavoriteRoutes(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiHost}/rides/favorites`);
  }

  addToFavorites(routeId: number): Observable<any> {
    return this.http.put(`${environment.apiHost}/rides/history/${routeId}/add-to-favorites`, {});
  }

  removeFromFavorites(routeId: number): Observable<void> {
    return this.http.delete<void>(
      `${environment.apiHost}/rides/favorites/by-favorite-id/${routeId}`,
    );
  }

  removeFromOtherFavorites(routeId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiHost}/rides/history/by-route-id/${routeId}`);
  }

  hasOngoingRide(): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiHost}/rides/ongoing`);
  }

  getReviews(id: number): Observable<Review[]>{
    return this.http.get<Review[]>(`${environment.apiHost}/reviews/${id}`)
  }

  getComplaints(id: number): Observable<Complaint[]>{
    return this.http.get<Complaint[]>(`${environment.apiHost}/reports/${id}`)
  }
}

export interface Review{
  username: string;
  comment: string;
  driverRating: number;
  vehicleRating: number;
}

export interface Complaint{
  username: string;
  content: string; 
}