import { User } from "./user.model";
import {Signal} from '@angular/core';

export interface Ride {
  id: number;
  startLocation: string;
  endLocation: string;
  startTime: Date;
  endTime: Date;
  price: number;
  passengers: User[];
  canceled: boolean;
  panic: boolean
}
