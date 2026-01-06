import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

interface FavoriteRoute {
  id: number;
  from: string;
  to: string;
  stops: string[];
}

@Component({
  selector: 'app-favorites-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites-popup.component.html',
  styleUrl: './favorites-popup.component.css',
})
export class FavoritesPopupComponent {
  @Output() routeSelected = new EventEmitter<FavoriteRoute>();
  @Output() closed = new EventEmitter<void>();

  favoriteRoutes: FavoriteRoute[] = [
    {
      id: 1,
      from: 'Kopernikova 23',
      to: 'Železnička stanica',
      stops: [],
    },
    {
      id: 2,
      from: 'Home',
      to: 'Airport Belgrade',
      stops: ['Gas Station', 'Highway Rest Stop'],
    },
    {
      id: 3,
      from: 'Apartment',
      to: 'Shopping Mall Delta',
      stops: ['Bank', 'Post Office'],
    },
    {
      id: 4,
      from: 'Office',
      to: 'Fitness Center',
      stops: [],
    },
  ];

  selectRoute(route: FavoriteRoute) {
    this.routeSelected.emit(route);
  }

  close() {
    this.closed.emit();
  }
}
