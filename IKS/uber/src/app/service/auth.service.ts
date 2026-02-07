import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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
    return this.http.post<any>(`${environment.authHost}/login`, creds, { observe: 'response' });
  }

  register(data: RegistrationData) {
    return this.http.post<any>(`${environment.authHost}/register`, data, { observe: 'response' });
  }

  verify(id: string) {
    return this.http.get<any>(`${environment.authHost}/verify/${id}`, { observe: 'response' });
  }

  checkSetPasswordToken(id: string) {
    return this.http.get<any>(`${environment.authHost}/set-password/${id}`, {
      observe: 'response',
    });
  }

  setPassword(id: string, password: string) {
    return this.http.post<any>(
      `${environment.authHost}/set-password/${id}`,
      { password: password },
      { observe: 'response' },
    );
  }

  requestPasswordReset(email: string) {
    return this.http.post<any>(
      `${environment.authHost}/forgot-password`,
      { email: email },
      { observe: 'response' },
    );
  }

  logout() {
    const token = localStorage.getItem('auth_token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
    console.log(this.getRole())
    if(this.getRole() === "driver"){
      this.http
        .put(`${environment.apiHost}/drivers/me/toggle-status?active=false`, null, { headers })
        .subscribe({
          error: (err) => {
            console.error('Error deactivating driver:', err);
          },
        });
    }
    this.performLogout();
  }

  private performLogout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('expires_in');
    this.role.set(null);
  }

  isLoggedIn() {
    const expiration = localStorage.getItem('expires_in');
    if (expiration) {
      const currentTime = Date.now();
      const isValid = currentTime < Number(expiration);
      return isValid;
    } else {
      return false;
    }
  }

  save(token: AuthToken) {
    const expirationTime = Date.now() + Number(token.expiresIn);
    localStorage.setItem('auth_token', token.accessToken);
    localStorage.setItem('expires_in', expirationTime.toString());
    this.updateRole();
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

  changePassword(changePasswordData: ChangePassword) {
    const token = localStorage.getItem('auth_token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put(`${environment.apiHost}/account/me/change-password`, changePasswordData, {
      headers,
      responseType: 'text',
    });
  }

  private updateRole() {
    const newRole = this.getRole();
    this.role.set(newRole);
  }
}

export interface ChangePassword {
  oldPassword: string;
  newPassword: string;
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

export interface AuthToken {
  accessToken: string;
  expiresIn: string;
}
