import {Injectable, signal, Signal} from '@angular/core';
import {Ride} from '../model/ride-history.model';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class RideService {

  private _users = signal<User[]>(
    [
      {
        id: 1,
        name: "Ivana Ignjatic",
        email: "ignjaticivana@gmail.com",
        phoneNumber: "066 066 00 00"
      },
      {
        id: 2,
        name: "Niksa Cvorovic",
        email: "cvorovicniksa@gmail.com",
        phoneNumber: "066 066 00 00"
      },
      {
        id: 3,
        name: "Bojana Paunovic",
        email: "paunovicbojana@gmail.com",
        phoneNumber: "066 066 00 00"
      },
      
      ])

  users = this._users.asReadonly();


  private _rides = signal<Ride[]>(
    [
      {
        id: 1,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 2,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 3,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 4,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 5,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 6,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 7,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      {
        id: 8,
        startLocation: "Alekse Santica 5, Novi Sad",
        destination: "Mileve Maric 40, Novi Sad",
        startTime: new Date('2025-12-14T10:30:00'),
        endTime: new Date('2025-12-14T10:44:00'),
        price: 824.00,
        users: this.users,
        canceled: false,
        panic:false
      },
      
      
      ])

  rides = this._rides.asReadonly()


}
