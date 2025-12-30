import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { User } from '../model/user.model';
import { HttpClient } from '@angular/common/http';
import { rxResource } from '@angular/core/rxjs-interop';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient)

  logged: User | null = null;

  getUser(creds: LoginCreds){
    return this.http.post<User>(`${environment.apiHost}/account/login`, creds)
  }

  registerUser(data: RegistrationData){
    return this.http.post(`${environment.apiHost}/account/register`, data)
  }

}

export interface LoginCreds{
  email: string,
  password: string
}

export interface RegistrationData {
  email: string;
  password: string;
  type: 'PASSENGER' | 'DRIVER' | 'ADMIN'
  name: string;
  lastName: string;
  homeAddress: string;
  phone: string;
  image: string;
}

