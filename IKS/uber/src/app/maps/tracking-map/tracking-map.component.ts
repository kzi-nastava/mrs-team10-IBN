import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter,
  Signal,
  input,
  OnDestroy,
} from '@angular/core';
import * as L from 'leaflet';
import { Observable, firstValueFrom, map } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import 'leaflet-routing-machine';
import { environment } from '../../../environments/environment';
import { Location } from '../../model/location.model';
import { output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ride, Station } from '../../model/ride-history.model';
import { TrackingData } from '../../layout/tracking-route/tracking-route.component';

interface RoutingOptionsWithMarker extends L.Routing.RoutingControlOptions {
  createMarker?: () => L.Marker | null;
}

@Component({
  selector: 'app-tracking-map',
  imports: [],
  templateUrl: './tracking-map.component.html',
  styleUrl: './tracking-map.component.css',
})
export class TrackingMapComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() stations: Station[] = [];
  @Input() interactive: boolean = true;
  rideId = input<number>();

  estimatedTime = output<string>();
  currentLocation = output<TrackingData>();
  stateChange = output<{ status: string; location: TrackingData | null }>();
  passingOutput = output<number>();
  distanceOutput = output<number>();

  private passedCount = 1;
  getTrackingVehicle: boolean = true;
  rideProgress: number = 0;
  coordinates: any[] = [];
  distance: number = 0;

  private map!: L.Map;
  private routeControl?: L.Routing.Control;
  private vehicleLayer!: L.LayerGroup;

  PinIcon!: L.Icon;
  PickupIcon!: L.Icon;
  DestinationIcon!: L.Icon;
  CurrentLocationIcon!: L.Icon;

  private points: {
    marker: L.Marker;
    latLng: L.LatLng;
    address?: string;
  }[] = [];

  private lastTrackingData: TrackingData | null = null;
  private currentLocationMarker?: L.Marker;
  private isUpdatingFromParent = false;

  constructor(private http: HttpClient) {}

  ngAfterViewInit(): void {
    this.clearAll();
    this.initializeIcons();
    this.initMap();
    this.vehicleLayer = L.layerGroup().addTo(this.map);
  }

  private initializeIcons(): void {
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

    this.CurrentLocationIcon = L.icon({
      iconUrl: 'current-loc.svg',
      iconSize: [16, 16],
      iconAnchor: [8, 8],
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['stations']) {
      const stationsValue = changes['stations'].currentValue;

      if (stationsValue && stationsValue.length > 0 && this.map) {
        this.coordinates = [];
        this.passedCount = 1;
        this.updateLocationsFromInput();
      }
    }
  }

  ngOnDestroy(): void {
    this.getTrackingVehicle = false;
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.242, 19.8227],
      zoom: 12.75,
      zoomSnap: 0.25,
      dragging: this.interactive,
      touchZoom: this.interactive,
      scrollWheelZoom: this.interactive,
      doubleClickZoom: this.interactive,
      boxZoom: this.interactive,
      keyboard: this.interactive,
      zoomControl: this.interactive,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
    this.passingOutput.emit(this.passedCount);
  }

  private async updateLocationsFromInput(): Promise<void> {
    if (!this.stations?.length) {
      console.warn('No stations provided');
      this.clearAll();
      return;
    }

    this.isUpdatingFromParent = true;
    this.clearAll();

    for (let i = 0; i < this.stations.length; i++) {
      const station = this.stations[i];
      if (station && station.lat && station.lon) {
        await this.addLocationFromAddress(station, i);
      } else {
        console.warn(`Station ${i} missing lat/lon or invalid:`, station);
      }
    }

    await this.drawInitialRoute();
    this.isUpdatingFromParent = false;

    if (this.coordinates.length > 0) {
      this.getVehiclePosition();
    }
  }

  private async addLocationFromAddress(station: Station, index: number): Promise<void> {
    try {
      if (!station.lat || !station.lon) {
        console.warn('Station missing lat/lon:', station);
        return;
      }

      const lat = station.lat;
      const lon = station.lon;

      let addressString = `${lat.toFixed(6)}, ${lon.toFixed(6)}`;
      try {
        const addressResult = await this.reverseSearch(lat, lon).toPromise();
        if (addressResult && addressResult.address) {
          const addr = addressResult.address;
          addressString = (addr.road || 'Unknown') + ' ' + (addr.house_number || '');
        }
      } catch (err) {
        console.warn('Could not get address from reverse geocoding:', err);
      }

      let icon = this.PinIcon;
      if (index === 0) {
        icon = this.PickupIcon;
      } else if (index === this.stations.length - 1) {
        icon = this.DestinationIcon;
      }

      this.addPointWithIcon(lat, lon, icon, addressString);
    } catch (error) {
      console.error(`Error processing station:`, error);
    }
  }

  private addPointWithIcon(lat: number, lng: number, icon: L.Icon, title?: string): void {
    const latLng = L.latLng(lat, lng);
    const pin = L.marker(latLng, { icon, title }).addTo(this.map);

    if (title) {
      pin.bindPopup(title);
    }

    this.points.push({ marker: pin, latLng, address: title });
  }

  private async drawInitialRoute(): Promise<void> {
    if (this.points.length < 2) {
      console.warn('Not enough points to draw route');
      if (this.routeControl) {
        this.map.removeControl(this.routeControl);
        this.routeControl = undefined;
      }
      return;
    }

    const waypoints = this.points.map((p) => p.latLng);

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

    return new Promise((resolve) => {
      this.routeControl?.on('routesfound', (e) => {
        const routes = e.routes;
        if (routes.length > 0) {
          const summary = routes[0].summary;
          this.coordinates = routes[0].coordinates;
          this.distance = routes[0].summary.totalDistance / 1000;

          this.estimatedTime.emit(Math.round((summary.totalTime % 3600) / 60) + ' minutes');

          if (this.points.length > 0 && !this.currentLocationMarker) {
            this.currentLocationMarker = L.marker(this.points[0].latLng, {
              icon: this.CurrentLocationIcon,
            }).addTo(this.map);
          }
        }
        resolve(undefined);
      });
    });
  }

  getTrackingData(position: L.LatLng): Observable<TrackingData> {
    const latRound: number = Number(position.lat.toFixed(7));
    const lonRound: number = Number(position.lng.toFixed(7));
    return this.reverseSearch(latRound, lonRound).pipe(
      map((response) => ({
        lat: latRound,
        lon: lonRound,
        address:
          (response.address?.road || 'Unknown') + ' ' + (response.address?.house_number || ''),
      })),
    );
  }

  searchStreet(street: string): Observable<any> {
    const query = encodeURIComponent(street + ', Novi Sad, Serbia');
    const url = `/nominatim/search?format=json&q=${query}`;
    return this.http.get(url);
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(`/nominatim/reverse?format=json&lat=${lat}&lon=${lon}`);
  }

  clearAll(): void {
    this.points.forEach((p) => this.map.removeLayer(p.marker));
    this.points = [];

    if (this.currentLocationMarker) {
      this.map.removeLayer(this.currentLocationMarker);
      this.currentLocationMarker = undefined;
    }

    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }
  }

  async getVehiclePosition() {
    if (!this.rideId() || this.coordinates.length === 0) {
      console.warn('Cannot start tracking: rideId or coordinates missing');
      return;
    }

    while (this.getTrackingVehicle) {
      try {
        const ride = await firstValueFrom(
          this.http.get<Ride>(`${environment.apiHost}/rides/${this.rideId()}`),
        );

        if (ride.status == 'Finished' || ride.status == 'Panic') {
          this.getTrackingVehicle = false;
          this.stateChange.emit({
            status: ride.status,
            location: this.lastTrackingData,
          });
          break;
        }

        const now = Date.now();
        const start = new Date(ride.startTime).getTime();
        const eta = new Date(ride.endTime).getTime();

        this.calculateRemainingTime(eta, now);

        this.rideProgress = 0;
        if (eta > start) {
          this.rideProgress = (now - start) / (eta - start);
        }
        this.rideProgress = Math.max(0, Math.min(1, this.rideProgress));

        if (!this.coordinates.length) {
          await this.sleep(3000);
          continue;
        }

        const index = Math.min(
          this.coordinates.length - 1,
          Math.floor(this.rideProgress * this.coordinates.length),
        );
        const pos = this.coordinates[index];

        if (this.currentLocationMarker) {
          this.currentLocationMarker.setLatLng(pos);
        }

        if (
          this.passedCount < this.points.length &&
          pos.distanceTo(this.points[this.passedCount].latLng) < 50
        ) {
          ++this.passedCount;
          this.passingOutput.emit(this.passedCount);
        }

        this.getTrackingData(pos).subscribe({
          next: (response) => {
            this.lastTrackingData = response;
            this.currentLocation.emit(response);
            this.distanceOutput.emit(this.distance * (index / this.coordinates.length));
          },
          error: (err) => {
            console.error('Error getting tracking data:', err);
          },
        });

        this.map.setView([pos.lat, pos.lng], 20);
      } catch (err) {
        console.error('Error in getVehiclePosition:', err);
      }

      await this.sleep(3000);
    }
  }

  calculateRemainingTime(eta: number, now: number) {
    const diffMs = Math.max(0, eta - now);
    this.estimatedTime.emit(`${Math.ceil(diffMs / 60000)} min`);
  }

  sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
