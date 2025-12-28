import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class RideRouteService {
  private route = signal<String[]>([]);
}
