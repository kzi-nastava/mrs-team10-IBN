import { AfterViewInit, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import 'leaflet-routing-machine';
import { environment } from '../../environments/environment';

interface RoutingOptionsWithMarker extends L.Routing.RoutingControlOptions {
  createMarker?: () => L.Marker | null;
}

interface Location {
  address: string;
  type: 'pickup' | 'stop' | 'destination';
  index?: number;
}

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements AfterViewInit, OnChanges {
  @Input() locations: Location[] = [];

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
  }[] = [];

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
    if (changes['locations'] && this.map) {
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

    this.clearAll();

    for (const location of this.locations) {
      await this.addLocationFromAddress(location);
    }
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

        this.addPointWithIcon(lat, lon, icon, location.address);
      }
    } catch (error) {
      console.error(`Error geocoding ${location.address}:`, error);
    }
  }

  private addPointWithIcon(lat: number, lng: number, icon: L.Icon, title?: string): void {
    const latLng = L.latLng(lat, lng);
    const pin = L.marker(latLng, { icon, title }).addTo(this.map);

    pin.on('click', () => {
      if (title) {
        pin.bindPopup(title).openPopup();
      }
    });

    this.points.push({ marker: pin, latLng });
    this.updateRoute();
  }

  private addPoint(lat: number, lng: number): void {
    this.addPointWithIcon(lat, lng, this.PinIcon);
  }

  public removeLastPoint(): void {
    if (this.points.length === 0) return;

    const last = this.points.pop();
    if (!last) return;

    this.map.removeLayer(last.marker);
    this.updateRoute();
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
      this.routeControl.on('routesfound', function (e) {
        var routes = e.routes;
        var summary = routes[0].summary;
        console.log(
          'Total distance is ' +
            summary.totalDistance / 1000 +
            ' km and total time is ' +
            Math.round((summary.totalTime % 3600) / 60) +
            ' minutes'
        );
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
