import {Component} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import {Router, RouterModule} from '@angular/router';
import { AuthService } from '../../service/auth.service';


/**
 * @title Toolbar overview
 */
@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatSidenavModule, RouterModule],
})
export class NavBarComponent {
  loggedIn: boolean;
  constructor(authService: AuthService){
    this.loggedIn = authService.isLoggedIn()
  }
}