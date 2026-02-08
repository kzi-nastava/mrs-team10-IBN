import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface VehicleType {
  id: number;
  name: string;
  price: number;
}

@Injectable({
  providedIn: 'root',
})
export class VehicleTypeService {
  private readonly defaultTypes: { [key: string]: VehicleType } = {
    standard: { id: 1, name: 'STANDARD', price: 120.0 },
    luxury: { id: 2, name: 'LUXURY', price: 200.0 },
    van: { id: 3, name: 'VAN', price: 150.0 },
  };

  constructor(private http: HttpClient) {}

  getVehicleTypes(): Observable<VehicleType[]> {
    return this.http.get<VehicleType[]>(`${environment.apiHost}/vehicle-types`);
  }

  mapTypeToDTO(typeString: string): VehicleType {
    const normalizedType = typeString.toLowerCase();
    return this.defaultTypes[normalizedType] || this.defaultTypes['standard'];
  }

  getTypeById(id: number): VehicleType | undefined {
    return Object.values(this.defaultTypes).find((type) => type.id === id);
  }
}
