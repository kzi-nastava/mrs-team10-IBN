import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-admin-home',
  imports: [
    NavBarComponent,
    MatTableModule,
    MatPaginatorModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css',
})
export class AdminHomeComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'id',
    'name',
    'lastname',
    'startLocation',
    'endLocation',
    'startTime',
    'endTime',
  ];
  dataSource = new MatTableDataSource<PeriodicElement>(ELEMENT_DATA);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}

export interface PeriodicElement {
  position: number;
  name: string;
  lastname: string;
  startLocation: string;
  endLocation: string;
  startTime: string;
  endTime: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {
    position: 1,
    name: 'John',
    lastname: 'Doe',
    startLocation: 'New York',
    endLocation: 'Los Angeles',
    startTime: '08:00',
    endTime: '18:00',
  },
  {
    position: 2,
    name: 'Jane',
    lastname: 'Smith',
    startLocation: 'Chicago',
    endLocation: 'Boston',
    startTime: '09:00',
    endTime: '17:00',
  },
  {
    position: 3,
    name: 'Robert',
    lastname: 'Johnson',
    startLocation: 'Miami',
    endLocation: 'Seattle',
    startTime: '07:30',
    endTime: '16:30',
  },
  {
    position: 4,
    name: 'Emily',
    lastname: 'Williams',
    startLocation: 'Denver',
    endLocation: 'Phoenix',
    startTime: '08:30',
    endTime: '17:30',
  },
  {
    position: 5,
    name: 'Michael',
    lastname: 'Brown',
    startLocation: 'San Francisco',
    endLocation: 'San Diego',
    startTime: '09:00',
    endTime: '18:00',
  },
  {
    position: 6,
    name: 'Sarah',
    lastname: 'Davis',
    startLocation: 'Austin',
    endLocation: 'Houston',
    startTime: '08:00',
    endTime: '17:00',
  },
  {
    position: 7,
    name: 'David',
    lastname: 'Miller',
    startLocation: 'Philadelphia',
    endLocation: 'Washington DC',
    startTime: '07:00',
    endTime: '16:00',
  },
  {
    position: 8,
    name: 'Jessica',
    lastname: 'Wilson',
    startLocation: 'Atlanta',
    endLocation: 'Charlotte',
    startTime: '08:15',
    endTime: '17:15',
  },
  {
    position: 9,
    name: 'Daniel',
    lastname: 'Moore',
    startLocation: 'Portland',
    endLocation: 'Vancouver',
    startTime: '09:30',
    endTime: '18:30',
  },
  {
    position: 10,
    name: 'Jennifer',
    lastname: 'Taylor',
    startLocation: 'Las Vegas',
    endLocation: 'Reno',
    startTime: '10:00',
    endTime: '19:00',
  },
  {
    position: 11,
    name: 'James',
    lastname: 'Anderson',
    startLocation: 'Orlando',
    endLocation: 'Tampa',
    startTime: '08:00',
    endTime: '17:00',
  },
  {
    position: 12,
    name: 'Mary',
    lastname: 'Thomas',
    startLocation: 'Nashville',
    endLocation: 'Memphis',
    startTime: '08:30',
    endTime: '17:30',
  },
  {
    position: 13,
    name: 'Christopher',
    lastname: 'Jackson',
    startLocation: 'Baltimore',
    endLocation: 'Philadelphia',
    startTime: '07:00',
    endTime: '16:00',
  },
  {
    position: 14,
    name: 'Linda',
    lastname: 'White',
    startLocation: 'Milwaukee',
    endLocation: 'Chicago',
    startTime: '08:00',
    endTime: '17:00',
  },
  {
    position: 15,
    name: 'Matthew',
    lastname: 'Harris',
    startLocation: 'Albuquerque',
    endLocation: 'Santa Fe',
    startTime: '09:00',
    endTime: '18:00',
  },
  {
    position: 16,
    name: 'Patricia',
    lastname: 'Martin',
    startLocation: 'Tucson',
    endLocation: 'Phoenix',
    startTime: '07:30',
    endTime: '16:30',
  },
  {
    position: 17,
    name: 'Mark',
    lastname: 'Thompson',
    startLocation: 'Long Beach',
    endLocation: 'Los Angeles',
    startTime: '08:00',
    endTime: '17:00',
  },
  {
    position: 18,
    name: 'Barbara',
    lastname: 'Garcia',
    startLocation: 'Fresno',
    endLocation: 'Sacramento',
    startTime: '08:30',
    endTime: '17:30',
  },
  {
    position: 19,
    name: 'Donald',
    lastname: 'Martinez',
    startLocation: 'Kansas City',
    endLocation: 'Saint Louis',
    startTime: '09:00',
    endTime: '18:00',
  },
  {
    position: 20,
    name: 'Susan',
    lastname: 'Robinson',
    startLocation: 'Mesa',
    endLocation: 'Phoenix',
    startTime: '08:00',
    endTime: '17:00',
  },
];
