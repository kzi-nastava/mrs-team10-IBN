import { User } from "./user.model";
import {Signal} from '@angular/core';

export interface Ride {
  id: number;
  startLocation: string;
  destination: string;
  startTime: Date;
  endTime: Date;
  price: number
  users: Signal<User[]>
}
