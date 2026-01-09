import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../../maps/map/map.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { RideApproxFormComponent } from "../../forms/ride-approx-form/ride-approx-form.component";
import { Location } from '../../model/location.model';

@Component({
  selector: 'app-home',
  imports: [RouterModule, MapComponent, NavBarComponent, RideApproxFormComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  routeOutput: Location[] = []
  estimatedTimeOutput: String = ""

  routeOutputEvent(eventData: Location[]){
    this.routeOutput = eventData
  }

  timeEstimationEvent(eventData: string){
    this.estimatedTimeOutput = eventData;
  }

  
}
