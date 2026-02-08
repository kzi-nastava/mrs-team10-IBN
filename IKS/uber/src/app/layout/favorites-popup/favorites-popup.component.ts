import { Component, EventEmitter, Output, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideService } from '../../service/ride-history.service';

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
export class FavoritesPopupComponent implements OnInit {
  constructor(
    private rs: RideService,
    private cdr: ChangeDetectorRef,
  ) {}

  @Output() routeSelected = new EventEmitter<FavoriteRoute>();
  @Output() closed = new EventEmitter<void>();
  @Output() routeRemoved = new EventEmitter<any>();
  @Input() editMode: boolean = false;

  favoriteRoutes: FavoriteRoute[] = [];
  loading: boolean = true;

  ngOnInit() {
    this.loadFavoriteRoutes();
  }

  private cleanAddress(address: string): string {
    if (!address) return '';

    return address
      .replace(/,\s*Novi Sad,\s*Serbia/i, '')
      .replace(/,\s*Serbia/i, '')
      .replace(/Novi Sad,\s*/i, '')
      .trim();
  }

  loadFavoriteRoutes() {
    this.loading = true;
    this.rs.getFavoriteRoutes().subscribe({
      next: (data) => {
        this.favoriteRoutes = data.map((fav) => this.mapToFavoriteRoute(fav));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading favorite routes:', error);
        this.loading = false;
        this.favoriteRoutes = [];
        this.cdr.detectChanges();
      },
    });
  }

  private mapToFavoriteRoute(dto: any): FavoriteRoute {
    if (!dto || !dto.routeDTO) {
      console.error('Invalid DTO structure:', dto);
      return {
        id: dto?.id || 0,
        from: '',
        to: '',
        stops: [],
      };
    }

    const route = dto.routeDTO;
    const stations = route.stations || [];

    const from =
      stations.length > 0 ? this.cleanAddress(stations[0].address || 'Unknown') : 'Unknown';

    const to =
      stations.length > 0
        ? this.cleanAddress(stations[stations.length - 1].address || 'Unknown')
        : 'Unknown';

    const stops =
      stations.length > 2
        ? stations
            .slice(1, -1)
            .map((station: any) => this.cleanAddress(station.address || 'Unknown'))
        : [];

    const mapped = {
      id: dto.id,
      from: from,
      to: to,
      stops: stops,
    };

    return mapped;
  }

  selectRoute(route: FavoriteRoute) {
    this.routeSelected.emit(route);
  }

  close() {
    this.closed.emit();
  }

  removeRoute(route: FavoriteRoute) {
    this.rs.removeFromFavorites(route.id).subscribe({
      next: () => {
        this.favoriteRoutes = this.favoriteRoutes.filter((r) => r.id !== route.id);
        this.routeRemoved.emit(route);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error removing favorite route:', error);
      },
    });
  }
}
