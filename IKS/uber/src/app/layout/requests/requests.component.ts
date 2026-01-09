import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { ChangeDetectorRef } from '@angular/core';

interface Request {
  id: number;
  type: 'profile' | 'vehicle' | 'both';
  driverName: string;
  requestDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'pending';
  changes: {
    oldData: { [key: string]: string };
    newData: { [key: string]: string };
  };
}

@Component({
  selector: 'app-requests',
  standalone: true,
  imports: [CommonModule, RouterModule, NavBarComponent],
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css'],
})
export class RequestsComponent implements OnInit {
  requests: Request[] = [];
  selectedRequest: Request | null = null;
  isLoading: boolean = false;

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.isLoading = true;

    this.http.get<Request[]>(`${environment.apiHost}/account/change-requests`).subscribe({
      next: (requests) => {
        this.requests = requests;
        this.isLoading = false;
        this.cd.detectChanges();
      },
      error: (error) => {
        console.error('Error loading requests:', error);
        this.isLoading = false;
      },
    });
  }

  openRequestDetails(request: Request) {
    this.selectedRequest = request;
  }

  closeModal() {
    this.selectedRequest = null;
  }

  handleApprove(id: number) {
    this.isLoading = true;

    this.http
      .post(`${environment.apiHost}/account/approve-change/${id}`, {}, { responseType: 'text' })
      .subscribe({
        next: () => {
          this.remove(id);
          this.closeModal();
          this.isLoading = false;
          this.cd.detectChanges();
        },
        error: (error) => {
          console.error('Error approving request:', error);
          this.isLoading = false;
        },
      });
  }

  handleReject(id: number) {
    this.isLoading = true;

    this.http
      .post(`${environment.apiHost}/account/reject-change/${id}`, {}, { responseType: 'text' })
      .subscribe({
        next: () => {
          this.remove(id);
          this.closeModal();
          this.isLoading = false;
          this.cd.detectChanges();
        },
        error: (error) => {
          console.error('Error rejecting request:', error);
          this.isLoading = false;
        },
      });
  }

  remove(id: number) {
    const index = this.requests.findIndex((req) => req.id === id);
    if (index !== -1) {
      this.requests.splice(index, 1);
    }
  }

  getChangeKeys(): string[] {
    if (!this.selectedRequest || !this.selectedRequest.changes) return [];

    const oldKeys = Object.keys(this.selectedRequest.changes.oldData || {});
    const newKeys = Object.keys(this.selectedRequest.changes.newData || {});

    return Array.from(new Set([...oldKeys, ...newKeys]));
  }

  isPending() {
    return this.selectedRequest?.status === 'PENDING' || this.selectedRequest?.status === 'pending';
  }
}
