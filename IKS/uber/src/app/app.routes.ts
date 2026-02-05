import { Routes } from '@angular/router';
import { LoginComponent } from './forms/login/login.component';
import { RegisterComponent } from './forms/register/register.component';
import { IncomingRideComponent } from './layout/incoming-ride/incoming-ride.component';
import { HomeComponent } from './layout/home/home.component';
import { AccountComponent } from './layout/account/account.component';
import { RideHistoryComponent } from './driver/ride-history/ride-history.component';
import { RequestsComponent } from './layout/requests/requests.component';
import { TrackingRouteComponent } from './layout/tracking-route/tracking-route.component';
import { VerifyFormComponent } from './forms/verify-form/verify-form.component';
import { ForgotPasswordComponent } from './forms/forgot-password/forgot-password.component';
import { ComplaintDialogComponent } from './passenger/complaint-dialog/complaint-dialog.component';
import { RateDriverVehicleComponent } from './passenger/rate-driver-vehicle/rate-driver-vehicle.component';
import { OrderRideComponent } from './layout/order-ride/order-ride.component';
import { RegisterDriverComponent } from './layout/register-driver/register-driver.component';
import { VehiclePriceComponent } from './forms/vehicle-price/vehicle-price.component';
import { AuthGuard } from './auth/auth-guard';
import { ScheduledRidesComponent } from './driver/scheduled-rides/scheduled-rides.component';
import { VerifyAccountComponent } from './layout/verify-account/verify-account.component';
import { NotificationTabComponent } from './layout/notification-tab/notification-tab.component';
import { ChatComponent } from './chat/chat.component';

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
    path: 'verify/:id',
    component: VerifyAccountComponent,
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
  },
  {
    path: 'set-password/:id',
    component: VerifyFormComponent,
  },
  {
    path: 'notifications',
    component: NotificationTabComponent,
    // canActivate: [AuthGuard],
    // data: { role: ['administrator', 'passenger', 'driver'] },
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
    data: { role: ['administrator', 'passenger', 'driver'] },
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
    path: 'tracking-route/:token',
    component: TrackingRouteComponent,
  },
  {
    path: 'tracking-route',
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
    path: 'rating/:rideId',
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
  {
    path: 'scheduled-rides',
    component: ScheduledRidesComponent,
    canActivate: [AuthGuard],
    data: { role: ['driver'] },
  },
  {
    path: 'chat',
    component: ChatComponent,
    canActivate: [AuthGuard],
    data: { role: ['driver', 'passenger', 'administrator'] },
  },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
