import { HttpClient } from '@angular/common/http';
import { signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { PageResponse } from '../model/page-response.model';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface DriversRides {
  name: string;
  lastname: string;
  startLocation: string;
  endLocation: string;
  startTime: string;
  endTime: string;
}

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  loadingData = false;
  hasMoreData = true;

  constructor(private http: HttpClient) {}

  private _currentRides = signal<DriversRides[]>([]);
  currentRides = this._currentRides.asReadonly();

  loadData(
    pageIndex: number,
    pageSize: number,
    search?: String,
  ): Observable<PageResponse<DriversRides>> {
    const url = `${environment.apiHost}/rides/adminView`;
    const params: any = {
      page: pageIndex.toString(),
      size: pageSize.toString(),
    };

    if (search && search.trim() !== '') {
      params.search = search;
    }

    return this.http.get<PageResponse<DriversRides>>(url, { params });
  }
}
