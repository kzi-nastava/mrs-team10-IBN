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
import { ForgotPasswordComponent } from './forms/forgot-password/forgot-password.component';
import { ComplaintDialogComponent } from './passenger/complaint-dialog/complaint-dialog.component';
import { RateDriverVehicleComponent } from './passenger/rate-driver-vehicle/rate-driver-vehicle.component';
import { OrderRideComponent } from './layout/order-ride/order-ride.component';
import { ManageUsersComponent } from './layout/manage-users/manage-users.component';
import { RegisterDriverComponent } from './layout/register-driver/register-driver.component';

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
    path: 'forgot-password',
    component: ForgotPasswordComponent,
  },
  {
    path: 'account',
    component: AccountComponent,
  },
  {
    path: 'ride-history',
    component: RideHistoryComponent,
  },
  {
    path: 'requests',
    component: RequestsComponent,
  },
  {
    path: 'incoming-ride',
    component: IncomingRideComponent,
  },
  {
    path: 'tracking-route',
    component: TrackingRouteComponent,
  },
  {
    path: 'complaint',
    component: ComplaintDialogComponent,
  },
  {
    path: 'rating',
    component: RateDriverVehicleComponent,
  },
  {
    path: 'order-ride',
    component: OrderRideComponent,
  },
  {
    path: 'manage-users',
    component: ManageUsersComponent,
  },
  {
    path: 'register-driver',
    component: RegisterDriverComponent,
  },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
