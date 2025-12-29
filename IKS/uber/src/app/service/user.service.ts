import { Injectable, signal } from '@angular/core';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private _logged = signal<User>({
    id: 0,
    name:'Petar Petrovic',
    email:'petar@gmail.com',
    role:'administrator',
    phoneNumber:'000000000'
  });

  logged = this._logged.asReadonly();
}
