import { Injectable, inject } from '@angular/core';
import { Location } from '../model/location.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Route, Station } from '../model/ride-history.model';
import { TrackingData } from '../layout/tracking-route/tracking-route.component';

@Injectable({
  providedIn: 'root',
})
export class RouteService {
  private readonly http = inject(HttpClient);

  getRide() {
    return this.http.get<RidePayload>(`${environment.apiHost}/rides/incoming`);
  }

  finishRide(id: number, time: string) {
    return this.http.post(`${environment.apiHost}/rides/finish/${id}`, { isotime: time });
  }

  startRide(id: number, time: string) {
    return this.http.put<void>(`${environment.apiHost}/rides/start/${id}`, {
      isotime: time,
    });
  }

  stopRide(id: number, passed: number, time: string, location: TrackingData) {
    const body = {
      "id":id,
      "passed":passed,
      "lat": location.lat,
      "lon": location.lon,
      "address":location.address,
      "finishTime":time
    }
    return this.http.post(`${environment.apiHost}/rides/stop`, body)
  }

  panic(id: number, passed:number, time:string, location:TrackingData){
    const body = {
      "id":id,
      "passed":passed,
      "lat": location.lat,
      "lon": location.lon,
      "address":location.address,
      "finishTime":time
    }
    return this.http.post(`${environment.apiHost}/rides/panic`, body)
  }

  route: Location[] = [
    {
      address: 'Bulevar oslobođenja 7',
      type: 'pickup',
    },
    {
      address: 'Bulevar Patrijarha Pavla 60',
      type: 'stop',
    },
    {
      address: 'Kornelija Stankovića 15',
      type: 'destination',
    },
  ];
}

export interface RidePayload {
  id: number;
  route: Route;
  startTime?: Date;
  endTime?: Date;
}
