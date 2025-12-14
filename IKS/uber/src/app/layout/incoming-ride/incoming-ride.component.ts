import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-incoming-ride',
  imports: [MatIconModule],
  templateUrl: './incoming-ride.component.html',
  styleUrl: './incoming-ride.component.css',
})
export class IncomingRideComponent {
  startloc: String = "Bulevar Oslobođenja 3";
  finaldest: String = "Hadži Ruvimova 7";
}
