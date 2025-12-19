import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../../map/map.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';

@Component({
  selector: 'app-home',
  imports: [RouterModule, MapComponent, NavBarComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {}
