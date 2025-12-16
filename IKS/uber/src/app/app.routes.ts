import { Routes } from '@angular/router';
import { LoginComponent } from './forms/login/login.component';
import { RegisterComponent } from './forms/register/register.component';
import { IncomingRideComponent } from './layout/incoming-ride/incoming-ride.component';
import { HomeComponent } from './layout/home/home.component';
import { AccountComponent } from './layout/account/account.component';
import { RideHistoryComponent } from './driver/ride-history/ride-history.component';
import { RequestsComponent } from './layout/requests/requests.component';
import { TrackingRouteComponent } from './layout/tracking-route/tracking-route.component';
import { VerifyAccountComponent } from './forms/verify-account/verify-account.component';


export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'verify',
    component: VerifyAccountComponent,
  },
  {
    path: 'account',
    component: AccountComponent,
  },
  {
    path: 'ride-history',
    component: RideHistoryComponent
  },
  {
    path: 'requests',
    component: RequestsComponent,
  },
  {
    path: 'incoming-ride',
    component: IncomingRideComponent
  },
  {
    path: 'tracking-route',
    component: TrackingRouteComponent
  },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
