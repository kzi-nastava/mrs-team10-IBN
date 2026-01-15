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
import { VehiclePriceComponent } from './forms/vehicle-price/vehicle-price.component';
import { AuthGuard } from './auth/auth-guard';

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
    canActivate: [AuthGuard],
    data: { role: ['passenger'] },
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator', 'passenger', 'driver'] },
  },
  {
    path: 'account',
    component: AccountComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator', 'passenger', 'driver'] },
  },
  {
    path: 'ride-history',
    component: RideHistoryComponent,
    canActivate: [AuthGuard],
    data: { role: ['passenger', 'driver'] },
  },
  {
    path: 'requests',
    component: RequestsComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator'] },
  },
  {
    path: 'incoming-ride',
    component: IncomingRideComponent,
    canActivate: [AuthGuard],
    data: { role: ['driver'] },
  },
  {
    path: 'tracking-route/:rideId',
    component: TrackingRouteComponent,
    canActivate: [AuthGuard],
    data: { role: ['passenger', 'driver'] },
  },
  {
    path: 'complaint',
    component: ComplaintDialogComponent,
    canActivate: [AuthGuard],
    data: { role: ['passenger'] },
  },
  {
    path: 'rating',
    component: RateDriverVehicleComponent,
    canActivate: [AuthGuard],
    data: { role: ['passenger'] },
  },
  {
    path: 'order-ride',
    component: OrderRideComponent,
    canActivate: [AuthGuard],
    data: { role: ['passenger'] },
  },
  {
    path: 'manage-users',
    component: ManageUsersComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator'] },
  },
  {
    path: 'vehicle-price',
    component: VehiclePriceComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator'] },
  },
  {
    path: 'register-driver',
    component: RegisterDriverComponent,
    canActivate: [AuthGuard],
    data: { role: ['administrator'] },
  },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
