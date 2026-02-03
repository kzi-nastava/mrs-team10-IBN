import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { AdminService, DriversRides } from '../../service/admin-rides.service';
import { UserService } from '../../service/user.service';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { BlockUserDialogComponent } from '../../forms/block-user-dialog/block-user-dialog.component';
import { UnblockUserDialogComponent } from '../../forms/block-user-dialog/unblock-user-dialog.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';

export interface UserData {
  name: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  accountStatus: string;
  blockingReason?: string;
}

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css'],
  standalone: true,
  imports: [
    NavBarComponent,
    MatTableModule,
    MatPaginatorModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatSnackBarModule,
    CommonModule,
    RouterModule,
  ],
})
export class AdminHomeComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    'name',
    'lastname',
    'startLocation',
    'endLocation',
    'startTime',
    'endTime',
  ];
  ridesDataSource = new MatTableDataSource<DriversRides>([]);
  totalData = 0;
  pageSize = 5;
  pageIndex = 0;
  searchTerm = '';

  @ViewChild('ridesPaginator') ridesPaginator!: MatPaginator;

  driversDisplayedColumns: string[] = [
    'name',
    'lastname',
    'email',
    'phoneNumber',
    'status',
    'actions',
  ];
  driversDataSource = new MatTableDataSource<UserData>([]);
  totalDrivers = 0;
  driversPageIndex = 0;

  @ViewChild('driversPaginator') driversPaginator!: MatPaginator;

  passengersDisplayedColumns: string[] = [
    'name',
    'lastname',
    'email',
    'phoneNumber',
    'status',
    'actions',
  ];
  passengersDataSource = new MatTableDataSource<UserData>([]);
  totalPassengers = 0;
  passengersPageIndex = 0;

  @ViewChild('passengersPaginator') passengersPaginator!: MatPaginator;

  constructor(
    private adminService: AdminService,
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadData();
    this.loadDrivers();
    this.loadPassengers();
  }

  ngAfterViewInit(): void {}

  onSearchChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchTerm = value;
    this.pageIndex = 0;
    this.loadData();
  }

  loadData(): void {
    this.adminService.loadData(this.pageIndex, this.pageSize, this.searchTerm).subscribe({
      next: (res) => {
        this.ridesDataSource.data = res.content;
        this.totalData = res.totalElements;
      },
      error: (err) => {
        console.error('Error fetching rides:', err);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  loadDrivers(): void {
    this.userService.getDrivers(this.driversPageIndex, 5).subscribe({
      next: (response) => {
        this.driversDataSource.data = response.content;
        this.totalDrivers = response.totalElements;
      },
      error: (error) => {
        console.error('Error loading drivers:', error);
      },
    });
  }

  blockDriver(driver: UserData): void {
    const dialogRef = this.dialog.open(BlockUserDialogComponent, {
      width: '500px',
      data: {
        name: driver.name,
        lastname: driver.lastname,
        userType: 'Driver',
      },
    });

    dialogRef.afterClosed().subscribe((reason) => {
      if (reason) {
        this.userService.blockUser(driver.email, reason).subscribe({
          next: () => {
            this.loadDrivers();
          },
          error: (error) => {
            console.error('Error blocking driver:', error);
          },
        });
      }
    });
  }

  unblockDriver(driver: UserData): void {
    const dialogRef = this.dialog.open(UnblockUserDialogComponent, {
      width: '500px',
      data: {
        name: driver.name,
        lastname: driver.lastname,
        userType: 'Driver',
        blockingReason: driver.blockingReason,
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.userService.unblockUser(driver.email).subscribe({
          next: () => {
            this.loadDrivers();
          },
          error: (error) => {
            console.error('Error unblocking driver:', error);
          },
        });
      }
    });
  }

  onDriversPageChange(event: PageEvent): void {
    this.driversPageIndex = event.pageIndex;
    this.loadDrivers();
  }

  loadPassengers(): void {
    this.userService.getPassengers(this.passengersPageIndex, 5).subscribe({
      next: (response) => {
        this.passengersDataSource.data = response.content;
        this.totalPassengers = response.totalElements;
      },
      error: (error) => {
        console.error('Error loading passengers:', error);
      },
    });
  }

  blockPassenger(passenger: UserData): void {
    const dialogRef = this.dialog.open(BlockUserDialogComponent, {
      width: '500px',
      data: {
        name: passenger.name,
        lastname: passenger.lastname,
        userType: 'Passenger',
      },
    });

    dialogRef.afterClosed().subscribe((reason) => {
      if (reason) {
        this.userService.blockUser(passenger.email, reason).subscribe({
          next: () => {
            this.loadPassengers();
          },
          error: (error) => {
            console.error('Error blocking passenger:', error);
          },
        });
      }
    });
  }

  unblockPassenger(passenger: UserData): void {
    const dialogRef = this.dialog.open(UnblockUserDialogComponent, {
      width: '500px',
      data: {
        name: passenger.name,
        lastname: passenger.lastname,
        userType: 'Passenger',
        blockingReason: passenger.blockingReason,
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.userService.unblockUser(passenger.email).subscribe({
          next: () => {
            this.loadPassengers();
          },
          error: (error) => {
            console.error('Error unblocking passenger:', error);
          },
        });
      }
    });
  }

  onPassengersPageChange(event: PageEvent): void {
    this.passengersPageIndex = event.pageIndex;
    this.loadPassengers();
  }
}
