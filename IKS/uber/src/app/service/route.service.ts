import { Injectable, inject } from '@angular/core';
import { Location } from '../model/location.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Route, Station } from '../model/ride-history.model';
import { TrackingData } from '../layout/tracking-route/tracking-route.component';
import { Ride } from '../model/ride-history.model';

@Injectable({
  providedIn: 'root',
})
export class RouteService {
  private readonly http = inject(HttpClient);

  getRide() {
    return this.http.get<RidePayload>(`${environment.apiHost}/rides/incoming`);
  }

  getOngoingRide(token: string) {
    return this.http.get<RidePayload>(`${environment.apiHost}/rides/tracking/${token}`);
  }

  getTrackingRide() {
    return this.http.get<RidePayload>(`${environment.apiHost}/rides/trackingRidePassenger`);
  }

  finishRide(id: number, time: string) {
    const body = {
      isotime: time,
    };
    return this.http.put(`${environment.apiHost}/rides/finish/${id}`, body);
  }

  startRide(id: number, time: string) {
    return this.http.put<void>(`${environment.apiHost}/rides/start/${id}`, {
      isotime: time,
    });
  }

  cancelRide(ride: RideCancellation) {
    return this.http.put(`${environment.apiHost}/rides/cancel`, ride);
  }

  stopRide(id: number, passed: number, time: string, location: TrackingData, distance: number) {
    const body = {
      id: id,
      passed: passed,
      lat: location.lat,
      lon: location.lon,
      address: location.address,
      distance: distance,
      finishTime: time,
    };
    return this.http.put(`${environment.apiHost}/rides/stop`, body);
  }

  panic(id: number, passed: number, time: string, location: TrackingData) {
    const body = {
      id: id,
      passed: passed,
      lat: location.lat,
      lon: location.lon,
      address: location.address,
      distance: 0,
      finishTime: time,
    };
    return this.http.put(`${environment.apiHost}/rides/panic`, body);
  }
}

export interface RidePayload {
  id: number;
  route: Route;
  startTime?: Date;
  endTime?: Date;
  token: string;
}

export interface RideCancellation {
  id: number;
  cancellationReason: string;
  cancelledByDriver: boolean;
}
