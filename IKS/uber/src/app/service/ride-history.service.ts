import {inject, Injectable, signal, Signal} from '@angular/core';
import {Ride} from '../model/ride-history.model';
import { User } from '../model/user.model';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { authGuard } from '../auth/auth-guard';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private authService = inject(AuthService);
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
    let role =  this.authService.role();
    //if (role == 'DRIVER')
      this.http.get<Ride[]>(`${environment.apiHost}/rides/driver`).subscribe(rides => this._rides.set(rides));
    //else if (role == 'PASSENGER')
    //  this.http.get<Ride[]>(`${environment.apiHost}/rides/passenger`).subscribe(rides => this._rides.set(rides));
}

  loadRideDetails(rideId : number) : Observable<Ride>{ 
    return this.http.get<Ride>(`${environment.apiHost}/rides/${rideId}`);
  }


}
