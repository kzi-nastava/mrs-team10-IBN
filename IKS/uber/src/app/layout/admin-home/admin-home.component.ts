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
import { DatePipe, CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { BlockUserDialogComponent } from '../../forms/block-user-dialog/block-user-dialog.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

export interface UserData {
  name: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  accountType: string;
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
    DatePipe,
    CommonModule,
  ],
})
export class AdminHomeComponent implements OnInit {
  rides: DriversRides[] = [];
  displayedColumns: string[] = [
    'name',
    'lastname',
    'startLocation',
    'endLocation',
    'startTime',
    'endTime',
  ];
  totalData = 0;
  pageSize = 5;
  pageIndex = 0;
  dataSource = new MatTableDataSource<DriversRides>();
  searchTerm = '';

  driversDataSource: UserData[] = [];
  driversDisplayedColumns: string[] = ['name', 'lastname', 'email', 'phoneNumber', 'actions'];
  totalDrivers: number = 0;
  driversPageIndex: number = 0;

  passengersDataSource: UserData[] = [];
  passengersDisplayedColumns: string[] = ['name', 'lastname', 'email', 'phoneNumber', 'actions'];
  totalPassengers: number = 0;
  passengersPageIndex: number = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

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

  onSearchChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchTerm = value;
    this.pageIndex = 0;
    this.loadData();
  }

  loadData(): void {
    this.adminService.loadData(this.pageIndex, this.pageSize, this.searchTerm).subscribe({
      next: (res) => {
        this.rides = res.content;
        this.totalData = res.totalElements;
        this.dataSource.data = this.rides;
      },
      error: (err) => console.error('Error fetching rides:', err),
    });
  }

  loadDrivers(): void {
    this.userService.getDrivers(this.driversPageIndex, 5).subscribe({
      next: (response) => {
        this.driversDataSource = response.content;
        this.totalDrivers = response.totalElements;
      },
      error: (error) => {
        console.error('Error loading drivers:', error);
        this.snackBar.open('Error loading drivers', 'Close', { duration: 3000 });
      },
    });
  }

  loadPassengers(): void {
    this.userService.getPassengers(this.passengersPageIndex, 5).subscribe({
      next: (response) => {
        this.passengersDataSource = response.content;
        this.totalPassengers = response.totalElements;
      },
      error: (error) => {
        console.error('Error loading passengers:', error);
        this.snackBar.open('Error loading passengers', 'Close', { duration: 3000 });
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
            this.snackBar.open('Driver blocked successfully', 'Close', { duration: 3000 });
            this.loadDrivers();
          },
          error: (error) => {
            console.error('Error blocking driver:', error);
            this.snackBar.open('Error blocking driver', 'Close', { duration: 3000 });
          },
        });
      }
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
            this.snackBar.open('Passenger blocked successfully', 'Close', { duration: 3000 });
            this.loadPassengers();
          },
          error: (error) => {
            console.error('Error blocking passenger:', error);
            this.snackBar.open('Error blocking passenger', 'Close', { duration: 3000 });
          },
        });
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  onDriversPageChange(event: PageEvent): void {
    this.driversPageIndex = event.pageIndex;
    this.loadDrivers();
  }

  onPassengersPageChange(event: PageEvent): void {
    this.passengersPageIndex = event.pageIndex;
    this.loadPassengers();
  }
}
