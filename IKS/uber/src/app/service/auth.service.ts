import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { User } from '../model/user.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient)

  login(creds: LoginCreds) {
    return this.http.post<any>(`${environment.authHost}/login`, creds, {observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        console.log(res)
        if (res.status == 200){
          localStorage.setItem("auth_token", res.body.accessToken)
          localStorage.setItem("expires_in", res.body.expiresIn)
          return true
        } else {
          return false
        }
      })
    )
  }

  register(data: RegistrationData){
    return this.http.post<any>(`${environment.authHost}/register`, data, {observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => res.status == 200)
    )
  }

  logout(){
    localStorage.removeItem("auth_token");
    localStorage.removeItem("expires_at");
  }

  isLoggedIn(){
    const expiration = localStorage.getItem("expires_at");
    if (expiration){
      return Math.floor(Date.now() / 1000) < Number(expiration)
    } else {
      return false
    }
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

