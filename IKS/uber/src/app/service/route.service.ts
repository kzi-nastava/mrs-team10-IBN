import { Injectable } from '@angular/core';
import { Location } from '../model/location.model';

@Injectable({
  providedIn: 'root',
})
export class RouteService {
  route: Location[] = [
    {
      address:'Bulevar oslobođenja 7',
      type:'pickup'
    },
    {
      address:'Bulevar Patrijarha Pavla 50',
      type:'stop'
    },
    {
      address:'Kornelija Stankovića 15',
      type:'destination'
    }
  ]
}
