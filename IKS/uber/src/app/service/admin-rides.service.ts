import { HttpClient } from '@angular/common/http';
import { signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { PageResponse } from '../model/page-response.model';

export interface DriversRides {
  id: number;
  name: string;
  lastname: string;
  startLocation: string;
  endLocation: string;
  startTime: string;
  endTime: string;
}

export class AdminService {
  dataPage = 0;
  pageSize = 5;

  loadingData = false;
  hasMoreData = true;

  constructor(private http: HttpClient) {}

  private _currentRides = signal<DriversRides[]>([]);
  currentRides = this._currentRides.asReadonly();

  loadData() {
    if (this.loadingData || !this.hasMoreData) return;
    this.loadingData = true;

    const url = `${environment.apiHost}/rides/admin-view}`;

    this.http
      .get<PageResponse<DriversRides>>(url, {
        params: {
          page: this.dataPage,
          size: this.pageSize,
        },
      })
      .subscribe((res) => {
        const current = this._currentRides();
        this._currentRides.set([...current, ...res.content]);
        this.dataPage++;
        this.hasMoreData = this.dataPage < res.totalPages;
        this.loadingData = false;
      });
  }
}
