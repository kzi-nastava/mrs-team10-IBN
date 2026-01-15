import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface CreateDriverDTO {
  accountDTO: {
    email: string;
  };
  createUserDTO: {
    name: string;
    lastName: string;
    homeAddress: string;
    phone: string;
    image: string;
  };
  vehicleDTO: {
    vehicleTypeDTO: {
      id: number | null;
      name: string;
      price: number;
    };
    model: string;
    plate: string;
    seatNumber: number;
    babySeat: boolean;
    petFriendly: boolean;
  };
}

export interface DriverDTO {
  accountDTO: {
    email: string;
  };
  createUserDTO: {
    name: string;
    lastName: string;
    homeAddress: string;
    phone: string;
    image: string;
  };
  vehicleDTO: {
    vehicleTypeDTO: {
      id: number | null;
      name: string;
      price: number;
    };
    model: string;
    plate: string;
    seatNumber: number;
    babySeat: boolean;
    petFriendly: boolean;
  };
  uptime: number;
}

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private apiUrl = `${environment.apiHost}/drivers`;

  constructor(private http: HttpClient) {}

  registerDriver(driverData: CreateDriverDTO): Observable<DriverDTO> {
    return this.http.post<DriverDTO>(this.apiUrl, driverData).pipe(catchError(this.handleError));
  }

  getDriverByUserId(userId: number): Observable<DriverDTO> {
    return this.http.get<DriverDTO>(`${this.apiUrl}/${userId}`).pipe(catchError(this.handleError));
  }

  getVehiclePositions(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(catchError(this.handleError));
  }

  updateDriverStatus(driverId: number, status: string): Observable<any> {
    return this.http
      .put(`${this.apiUrl}/${driverId}`, { status })
      .pipe(catchError(this.handleError));
  }

  submitDriverChangeRequest(driverId: number, changeRequest: any): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/${driverId}/change-request`, changeRequest)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;

      if (error.error?.message) {
        errorMessage = error.error.message;
      }
    }

    console.error('DriverService Error:', errorMessage);
    return throwError(() => error);
  }
}
