import { AfterViewInit, Component, inject, ChangeDetectorRef } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { RouterLink } from '@angular/router';
import { MapComponent } from "../../maps/map-basic/map.component";
import { Location } from '../../model/location.model';
import { RouteService } from '../../service/route.service';
import { Station } from '../../model/ride-history.model';

@Component({
  selector: 'app-incoming-ride',
  imports: [MatIconModule, NavBarComponent, RouterLink, MapComponent],
  templateUrl: './incoming-ride.component.html',
  styleUrl: './incoming-ride.component.css',
})
export class IncomingRideComponent{
  RouteService: RouteService = inject(RouteService)
  private cdr = inject(ChangeDetectorRef);

  route: Station[] = [];

  constructor(){
    this.RouteService.getRide().subscribe({
      next: (response) => {
        this.route = [...response.route.stations];
        console.log(this.route)
        this.cdr.detectChanges();
      }
    })
  }
}
