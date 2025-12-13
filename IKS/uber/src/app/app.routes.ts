import { Routes } from '@angular/router';
import { LoginComponent } from './forms/login/login.component';
import { RegisterComponent } from './forms/register/register.component';
import { HomeComponent } from './layout/home/home.component';
import { AccountComponent } from './layout/account/account.component';
import { RequestsComponent } from './layout/requests/requests.component';

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
    path: 'account',
    component: AccountComponent,
  },
  {
    path: 'requests',
    component: RequestsComponent,
  },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
