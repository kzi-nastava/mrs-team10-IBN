import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { catchError, map, tap } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly jwtHelper = new JwtHelperService();

  public readonly role = signal<string | null>(this.getRole());

  login(creds: LoginCreds) {
    return this.http
      .post<any>(`${environment.authHost}/login`, creds, { observe: 'response' })
      .pipe(
        map((res: HttpResponse<any>) => {
          if (res.status == 200) {
            const expirationTime = Math.floor(Date.now() / 1000) + Number(res.body.expiresIn);

            localStorage.setItem('auth_token', res.body.accessToken);
            localStorage.setItem('expires_in', expirationTime.toString());

            this.updateRole();

            return true;
          } else {
            return false;
          }
        })
      );
  }

  register(data: RegistrationData) {
    return this.http.post<any>(`${environment.authHost}/register`, data, { observe: 'response' })
  }

  verify(id: string){
    return this.http
      .get<any>(`${environment.authHost}/verify/${id}`, { observe: 'response' })
      .pipe(map((res: HttpResponse<any>) => res.status >= 200 && res.status < 300))
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('expires_in');
    this.role.set(null);
  }

  isLoggedIn() {
    const expiration = localStorage.getItem('expires_in');
    if (expiration) {
      const currentTime = Math.floor(Date.now() / 1000);
      const isValid = currentTime < Number(expiration);
      return isValid;
    } else {
      return false;
    }
  }

  private getRole(): string | null {
    if (this.isLoggedIn()) {
      const accessToken = localStorage.getItem('auth_token');
      if (accessToken) {
        try {
          const decodedToken = this.jwtHelper.decodeToken(accessToken);
          return decodedToken.roles || null;
        } catch (error) {
          console.error('Error decoding token:', error);
          return null;
        }
      }
    }
    return null;
  }

  private updateRole() {
    const newRole = this.getRole();
    this.role.set(newRole);
  }
}

export interface LoginCreds {
  email: string;
  password: string;
}

export interface RegistrationData {
  email: string;
  password: string;
  type: 'PASSENGER' | 'DRIVER' | 'ADMINISTRATOR';
  name: string;
  lastName: string;
  homeAddress: string;
  phone: string;
  image: string;
}
