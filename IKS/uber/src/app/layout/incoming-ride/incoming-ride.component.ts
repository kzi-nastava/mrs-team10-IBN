import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-incoming-ride',
  imports: [MatIconModule, NavBarComponent, RouterLink],
  templateUrl: './incoming-ride.component.html',
  styleUrl: './incoming-ride.component.css',
})
export class IncomingRideComponent {
  startloc: String = "Bulevar Oslobođenja 3";
  finaldest: String = "Hadži Ruvimova 7";
}
