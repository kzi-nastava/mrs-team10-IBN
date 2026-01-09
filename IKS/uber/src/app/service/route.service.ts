import { Injectable, inject } from '@angular/core';
import { Location } from '../model/location.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Route, Station } from '../model/ride-history.model';

@Injectable({
  providedIn: 'root',
})
export class RouteService {
  private readonly http = inject(HttpClient);

  getRide(){
    return this.http.get<RidePayload>(`${environment.apiHost}/rides/incoming`);
  }
  
  route: Location[] = [
    {
      address:'Bulevar oslobođenja 7',
      type:'pickup'
    },
    {
      address:'Bulevar Patrijarha Pavla 60',
      type:'stop'
    },
    {
      address:'Kornelija Stankovića 15',
      type:'destination'
    }
  ]
}

export interface RidePayload{
  id: number,
  route: Route,
  startTime?: Date,
  endTime?: Date
}
