// requests.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavBarComponent } from '../nav-bar/nav-bar.component';

interface Request {
  id: number;
  type: 'profile' | 'vehicle';
  driverName: string;
  requestDate: string;
  status: 'pending' | 'approved' | 'rejected';
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

  ngOnInit() {
    this.requests = [
      {
        id: 1,
        type: 'profile',
        driverName: 'Marko Petrovic',
        requestDate: '2024-12-10',
        status: 'pending',
        changes: {
          oldData: {
            Phone: '+381 62 123 4567',
            Email: 'marko@gmail.com',
          },
          newData: {
            Phone: '+381 63 999 8888',
            Email: 'marko.petrovic@gmail.com',
          },
        },
      },
      {
        id: 2,
        type: 'vehicle',
        driverName: 'Ana Jovanovic',
        requestDate: '2024-12-11',
        status: 'pending',
        changes: {
          oldData: {
            License: 'BG-123-AB',
            Model: 'VW Golf',
          },
          newData: {
            License: 'NS-456-CD',
            Model: 'VW Passat',
          },
        },
      },
      {
        id: 3,
        type: 'profile',
        driverName: 'Stefan Nikolic',
        requestDate: '2024-12-09',
        status: 'pending',
        changes: {
          oldData: {
            Adress: 'Bulevar Oslobodjenja 20',
          },
          newData: {
            Adress: 'Cara Dusana 15',
          },
        },
      },
    ];
  }

  openRequestDetails(request: Request) {
    this.selectedRequest = request;
  }

  closeModal() {
    this.selectedRequest = null;
  }

  handleApprove(id: number) {
    this.requests = this.requests.map((req) =>
      req.id === id ? { ...req, status: 'approved' as const } : req
    );
    this.remove(id);
    this.closeModal();
  }

  remove(id: number) {
    const index = this.requests.findIndex((req) => req.id === id);
    if (index !== -1) {
      this.requests.splice(index, 1);
    }
  }

  handleReject(id: number) {
    this.requests = this.requests.map((req) =>
      req.id === id ? { ...req, status: 'rejected' as const } : req
    );
    this.remove(id);
    this.closeModal();
  }

  getChangeKeys(): string[] {
    return this.selectedRequest ? Object.keys(this.selectedRequest.changes.oldData) : [];
  }
}
