import {Component, Signal} from '@angular/core';
import {Ride} from '../../model/ride-history.model'
import { RideService } from '../../service/ride-history.service';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-ride-history',
  imports: [NavBarComponent, DatePipe],
  templateUrl: 'ride-history.component.html',
  styleUrl: 'ride-history.component.css',
})
export class RideHistoryComponent {
  protected rides: Signal<Ride[]>;

  constructor(private service: RideService){
    this.rides = this.service.rides;
  }
}
