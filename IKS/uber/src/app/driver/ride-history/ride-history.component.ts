import {Component, Signal} from '@angular/core';
import {Ride} from '../../model/ride-history.model'
import { RideService } from '../../service/ride-history.service';


@Component({
  selector: 'app-ride-history',
  imports: [],
  templateUrl: 'ride-history.component.html',
  styleUrl: 'ride-history.component.css',
})
export class RideHistoryComponent {
  protected rides: Signal<Ride[]>;

  constructor(private service: RideService){
    this.rides = this.service.rides;
  }
}
