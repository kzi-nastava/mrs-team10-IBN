import { User } from "./user.model";
import { Location } from "./location.model";
export interface Station {
  id: number;
  lat: number;
  lon: number;
  address: string;
}

export interface Route {
  id: number;
  stations: Station[];
}

export interface vehicleLocation{
  latitude: number,
  longitude: number,
  address: string
}

export interface Ride {
  status: 'Pending' | 'Ongoing' | 'CancelledByDriver' | 'CancelledByPassenger' | 'Panic' | 'Finished';
  id: number;
  startLocation: string;
  endLocation: string;
  route: Route;
  startTime: Date;
  endTime: Date,
  estimatedTimeArrival: Date;
  price: number;
  passengers: User[];
  canceled: boolean;
  panic: boolean
  isBusy: boolean
  vehicleLocation: vehicleLocation
}
