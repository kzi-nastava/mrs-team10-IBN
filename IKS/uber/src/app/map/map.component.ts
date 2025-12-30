import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter,
} from '@angular/core';
import * as L from 'leaflet';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import 'leaflet-routing-machine';
import { environment } from '../../environments/environment';
import { Location } from '../model/location.model';
import { output } from '@angular/core';
import { CommonModule } from '@angular/common';

interface RoutingOptionsWithMarker extends L.Routing.RoutingControlOptions {
  createMarker?: () => L.Marker | null;
}

@Component({
  selector: 'app-map',
  imports: [CommonModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements AfterViewInit, OnChanges {
  @Input() showRemoveButton!: boolean;
  @Input() locations: Location[] = [];
  @Output() locationAdded = new EventEmitter<string>();
  @Output() locationRemoved = new EventEmitter<number>();
  @Output() allLocationsCleared = new EventEmitter<void>();
  estimatedTime = output<string>();

  private map!: L.Map;
  private routeControl?: L.Routing.Control;

  PinIcon!: L.Icon;
  PickupIcon!: L.Icon;
  DestinationIcon!: L.Icon;
  GreenCarIcon!: L.Icon;
  RedCarIcon!: L.Icon;

  private points: {
    marker: L.Marker;
    latLng: L.LatLng;
    address?: string;
  }[] = [];

  private isUpdatingFromParent = false;

  constructor(private http: HttpClient) {}

  ngAfterViewInit(): void {
    delete (L.Icon.Default.prototype as any)._getIconUrl;

    this.PinIcon = L.icon({
      iconUrl: 'pointer-pin.svg',
      iconSize: [32, 32],
      iconAnchor: [16, 32],
    });

    this.PickupIcon = L.icon({
      iconUrl: 'pointer-pin.svg',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
    });

    this.DestinationIcon = L.icon({
      iconUrl: 'pointer-pin.svg',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
    });

    this.GreenCarIcon = L.icon({
      iconUrl: 'green-car.png',
      iconSize: [38, 38],
      iconAnchor: [22, 94],
    });

    this.RedCarIcon = L.icon({
      iconUrl: 'red-car.png',
      iconSize: [38, 38],
      iconAnchor: [22, 94],
    });

    this.initMap();
    this.registerOnClick();

    if (this.locations && this.locations.length > 0) {
      this.updateLocationsFromInput();
    }

    L.marker([45.2519, 19.8456], { icon: this.GreenCarIcon }).addTo(this.map);
    L.marker([45.2222, 19.808], { icon: this.RedCarIcon }).addTo(this.map);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['locations'] && this.map && !this.isUpdatingFromParent) {
      this.updateLocationsFromInput();
    }
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
  }

  private async updateLocationsFromInput(): Promise<void> {
    if (!this.locations || this.locations.length === 0) {
      this.clearAll();
      return;
    }

    this.isUpdatingFromParent = true;
    this.clearAll();

    for (const location of this.locations) {
      await this.addLocationFromAddress(location);
    }

    this.isUpdatingFromParent = false;
  }

  private async addLocationFromAddress(location: Location): Promise<void> {
    try {
      const result = await this.searchStreet(location.address).toPromise();

      if (result && result.length > 0) {
        const lat = +result[0].lat;
        const lon = +result[0].lon;

        let icon = this.PinIcon;
        if (location.type === 'pickup') {
          icon = this.PickupIcon;
        } else if (location.type === 'destination') {
          icon = this.DestinationIcon;
        }

        this.addPointWithIcon(lat, lon, icon, location.address, true);
      }
    } catch (error) {
      console.error(`Error geocoding ${location.address}:`, error);
    }
  }

  private addPointWithIcon(
    lat: number,
    lng: number,
    icon: L.Icon,
    title?: string,
    fromParent = false
  ): void {
    const latLng = L.latLng(lat, lng);
    const pin = L.marker(latLng, { icon, title }).addTo(this.map);

    pin.on('click', () => {
      const index = this.points.findIndex((p) => p.marker === pin);
      if (index !== -1) {
        this.removePointByIndex(index);
      }
    });

    if (title) {
      pin.bindPopup(title);
    }

    this.points.push({ marker: pin, latLng, address: title });
    this.updateRoute();
  }

  private async addPoint(lat: number, lng: number): Promise<void> {
    try {
      const result = await this.reverseSearch(lat, lng).toPromise();

      let address = '';
      if (result && result.address) {
        const parts = [];
        if (result.address.road) parts.push(result.address.road);
        if (result.address.house_number) parts.push(result.address.house_number);
        address = parts.join(' ') || result.display_name;
      }

      this.addPointWithIcon(lat, lng, this.PinIcon, address);

      if (address) {
        this.locationAdded.emit(address);
      }
    } catch (error) {
      console.error('Error reverse geocoding:', error);
      this.addPointWithIcon(lat, lng, this.PinIcon);
    }
  }

  private removePointByIndex(index: number): void {
    if (index < 0 || index >= this.points.length) return;

    const point = this.points[index];
    this.map.removeLayer(point.marker);
    this.points.splice(index, 1);

    this.updateRoute();

    this.locationRemoved.emit(index);
  }

  public removeLastPoint(): void {
    if (this.points.length === 0) return;

    const lastIndex = this.points.length - 1;
    this.removePointByIndex(lastIndex);
  }

  private updateRoute(): void {
    if (this.points.length < 2) {
      if (this.routeControl) {
        this.map.removeControl(this.routeControl);
        this.routeControl = undefined;
      }
      return;
    }

    const waypoints = this.points.map((p) => p.latLng);

    if (this.routeControl) {
      this.routeControl.setWaypoints(waypoints);
    } else {
      const options: RoutingOptionsWithMarker = {
        waypoints,
        addWaypoints: false,
        show: false,
        createMarker: () => null,
        router: L.routing.mapbox(environment.apiKey, {
          profile: 'mapbox/driving',
        }),
      };

      this.routeControl = L.Routing.control(options).addTo(this.map);
      this.routeControl.on('routesfound',  (e) => {
        var routes = e.routes;
        var summary = routes[0].summary;
        console.log(
          'Total distance is ' +
            summary.totalDistance / 1000 +
            ' km and total time is ' +
            Math.round((summary.totalTime % 3600) / 60) +
            ' minutes'
        );
        this.estimatedTime.emit(Math.round((summary.totalTime % 3600) / 60) + ' minutes')
      });
    }
  }


  private registerOnClick(): void {
    this.map.on('click', (e: any) => {
      this.addPoint(e.latlng.lat, e.latlng.lng);
    });
  }

  searchStreet(street: string): Observable<any> {
    return this.http.get(
      'https://nominatim.openstreetmap.org/search?format=json&q=' + street + ', Novi Sad, Serbia'
    );
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`
    );
  }

  clearAll(): void {
    this.points.forEach((p) => this.map.removeLayer(p.marker));
    this.points = [];

    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }
  }
}
