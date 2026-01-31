import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { AdminService, DriversRides } from '../../service/admin-rides.service';
import { MatTableModule } from '@angular/material/table';
import { DatePipe } from '@angular/common';

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
    DatePipe,
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

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadData();
  }
  searchTerm = '';

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

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }
}
