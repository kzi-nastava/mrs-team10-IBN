import {Injectable, signal, Signal} from '@angular/core';
import {Ride} from '../model/ride-history.model';
import { User } from '../model/user.model';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RideService {
    protected user: User | null;
  
  constructor(private http: HttpClient) {
    let logged = sessionStorage.getItem("loggedUser")
    if (logged != null){
      this.user = JSON.parse(logged) as User
    } else {
      this.user = null
    }
    console.log(this.user)
    this.loadRides(this.user);
  }

  private _rides = signal<Ride[]>([]);
  rides = this._rides.asReadonly();

  loadRides(user : User | null) {
  if (!user) return; 

  this.http.get<Ride[]>(`${environment.apiHost}/rides`, {
    params: { userId: user.id }
  }).subscribe(rides => this._rides.set(rides));

  console.log(this.rides)
}

  loadRideDetails(rideId : number) : Observable<Ride>{ 
    return this.http.get<Ride>(`${environment.apiHost}/rides/${rideId}`);
  }


}
