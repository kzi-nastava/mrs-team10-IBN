import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StatisticsService } from '../../service/statistics.service';
import { AuthService } from '../../service/auth.service';
import { ChartComponent } from '../chart/chart.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';

interface DateRange {
  startDate: string;
  endDate: string;
}

interface RideStatistics {
  dailyRides: { date: string; value: number }[];
  dailyDistance: { date: string; value: number }[];
  dailyMoney: { date: string; value: number }[];
  totalRides: number;
  totalDistance: number;
  totalMoney: number;
  averageRides: number;
  averageDistance: number;
  averageMoney: number;
}

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule, FormsModule, ChartComponent, NavBarComponent],
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css'],
})
export class StatisticsComponent implements OnInit {
  userRole: string | null = 'passenger';

  dateRange: DateRange = {
    startDate: this.getDefaultStartDate(),
    endDate: this.getDefaultEndDate(),
  };

  statistics: RideStatistics | null = null;
  loading = false;
  error: string | null = null;

  viewMode: 'all' | 'single' = 'all';
  userType: 'drivers' | 'passengers' = 'drivers';
  selectedUserId: number | null = null;
  availableUsers: Array<{ id: number; name: string }> = [];

  constructor(
    private statisticsService: StatisticsService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.userRole = this.authService.role();
    console.log('User role:', this.userRole);

    if (this.userRole === 'administrator') {
      this.loadAvailableUsers();
    }

    this.loadStatistics();
  }

  getDefaultStartDate(): string {
    const date = new Date();
    date.setMonth(date.getMonth() - 1);
    return date.toISOString().split('T')[0];
  }

  getDefaultEndDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  loadStatistics() {
    console.log('loadStatistics called');

    if (this.userRole === 'administrator' && this.viewMode === 'single' && !this.selectedUserId) {
      this.statistics = null;
      return;
    }

    this.loading = true;
    this.error = null;
    this.statistics = null;

    let request;

    if (this.userRole === 'administrator') {
      if (this.viewMode === 'all') {
        request = this.statisticsService.getAllUsersStatistics(
          this.dateRange.startDate,
          this.dateRange.endDate,
          this.userType,
        );
      } else {
        request = this.statisticsService.getUserStatistics(
          this.selectedUserId!,
          this.dateRange.startDate,
          this.dateRange.endDate,
        );
      }
    } else {
      request = this.statisticsService.getMyStatistics(
        this.dateRange.startDate,
        this.dateRange.endDate,
      );
    }

    request.subscribe({
      next: (data) => {
        this.statistics = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error:', err);
        this.error = 'Failed to load statistics';
        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }

  loadAvailableUsers() {
    this.statisticsService.getUsers(this.userType).subscribe({
      next: (users) => {
        this.availableUsers = users;
        this.selectedUserId = null;
        if (this.viewMode === 'single') {
          this.statistics = null;
        }
      },
      error: (err) => console.error('Failed to load users', err),
    });
  }

  onDateRangeChange() {
    this.loadStatistics();
  }

  onViewModeChange() {
    if (this.viewMode === 'all') {
      this.loadStatistics();
    } else {
      this.statistics = null;
    }
  }

  onUserTypeChange() {
    this.loadAvailableUsers();
  }

  onUserChange() {
    if (this.viewMode === 'single') {
      if (this.selectedUserId) {
        this.loadStatistics();
      } else {
        this.statistics = null;
      }
    }
  }

  isAdmin(): boolean {
    return this.userRole === 'administrator';
  }

  getMoneyLabel(): string {
    if (this.userRole === 'administrator' && this.userType === 'drivers') {
      return 'Earned';
    }
    if (this.userRole === 'driver') {
      return 'Earned';
    }
    return 'Spent';
  }
}
