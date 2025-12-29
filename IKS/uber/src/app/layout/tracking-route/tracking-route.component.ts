import { Component, inject } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tracking-route',
  imports: [NavBarComponent],
  templateUrl: './tracking-route.component.html',
  styleUrl: './tracking-route.component.css',
})
export class TrackingRouteComponent {
  router: Router = inject(Router);
  timeType: String = "departure";
  time: String = "17:16";
  routeStarted: Boolean = false;
  firstButtonText: String = "Start";

  changeState(){
    if(this.routeStarted){
      this.router.navigate(['/home'])
    }
    this.routeStarted = true;
    this.firstButtonText = "Finish";
    this.timeType = "arrival"
  }
}
