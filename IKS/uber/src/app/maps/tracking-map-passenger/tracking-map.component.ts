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
import { environment } from '../../../environments/environment';
import { Location } from '../../model/location.model';
import { output } from '@angular/core';
import { Station } from '../../model/ride-history.model';
import { Ride } from '../../model/ride-history.model';
import { firstValueFrom } from 'rxjs';

interface RoutingOptionsWithMarker extends L.Routing.RoutingControlOptions {
  createMarker?: () => L.Marker | null;
}

@Component({
  selector: 'app-tracking-map',
  imports: [],
  templateUrl: '../tracking-map/tracking-map.component.html',
  styleUrl: '../tracking-map/tracking-map.component.css',
})


export class TrackingMapComponent implements AfterViewInit {
  @Input() interactive: boolean = true;
  @Output() locationRemoved = new EventEmitter<number>();
  @Output() allLocationsCleared = new EventEmitter<void>();
  @Input() stations: Station[] = [];
  estimatedTime = output<string>();

  private map!: L.Map;
  private routeControl?: L.Routing.Control;
  private getTrackingVehicle: boolean;
  private vehicleLayer!: L.LayerGroup;
  private rideProgress: number;

  PinIcon!: L.Icon;
  PickupIcon!: L.Icon;
  DestinationIcon!: L.Icon;
  CarIcon!: L.Icon;

  private points: {
    marker: L.Marker;
    latLng: L.LatLng;
    address?: string;
  }[] = [];


  constructor(private http: HttpClient) {
    this.getTrackingVehicle = true;
    this.rideProgress = 0
  }

  ngAfterViewInit(): void {
    delete (L.Icon.Default.prototype as any)._getIconUrl;

    this.CarIcon = L.icon({
      iconUrl: 'auto.png',
      iconSize: [40, 40],
      iconAnchor: [20, 20],
    });

    this.initMap();

    this.vehicleLayer = L.layerGroup().addTo(this.map);

    setTimeout(() => {
      this.map.invalidateSize();

      this.getVehiclePosition();

    }, 100);
  }

  ngOnDestroy(): void {
    this.getTrackingVehicle = false;
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
      zoomControl: this.interactive
    });


    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
  }


  private updateRoute(waypoints: L.Routing.Waypoint[]): void {

    if (this.routeControl) {
      this.routeControl.setWaypoints(waypoints);
    } else {
      const options: RoutingOptionsWithMarker = {
        waypoints,
        addWaypoints: false,
        show: false,
        fitSelectedRoutes: false,
        createMarker: () => null,
        router: L.routing.mapbox(environment.apiKey, {
          profile: 'mapbox/driving',
        }),
      };

      this.routeControl = L.Routing.control(options).addTo(this.map);
      this.routeControl.on('routesfound', (e) => {
        var routes = e.routes;
        const coords = routes[0].coordinates;
        if (!coords?.length) return;

        const index = Math.min(coords.length - 1, Math.floor(this.rideProgress * coords.length));
        const pos = coords[index];

        L.marker([pos.lat, pos.lng], { icon: this.CarIcon })
          .addTo(this.vehicleLayer);

        this.map.setView([pos.lat, pos.lng], 20);

        // this.estimatedTime.emit(Math.round((summary.totalTime % 3600) / 60) + ' minutes')
      });

    }
  }


  clearAll(): void {
    this.points.forEach((p) => this.map.removeLayer(p.marker));
    this.points = [];

    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }
  }


  async getVehiclePosition() {
    while (this.getTrackingVehicle) {
      try {
        const ride = await firstValueFrom(
          this.http.get<Ride>(`${environment.apiHost}/rides/trackingRidePassenger`)
        );
        if (ride.route == null)
          break

        const now = Date.now();
        const start = new Date(ride.startTime).getTime();
        const eta = new Date(ride.estimatedTimeArrival).getTime();

        this.calculateRemainingTime(eta, now);

        this.rideProgress = 0;
        if (eta > start) this.rideProgress = (now - start) / (eta - start);
        this.rideProgress = Math.max(0, Math.min(1, this.rideProgress));

        const waypoints = ride.route.stations
          .filter(s => s.lat != null && s.lon != null)
          .map(s => L.Routing.waypoint(new L.LatLng(s.lat, s.lon)));

        if (waypoints.length < 2) continue;

        this.updateRoute(waypoints)
      }

      catch (err) {
        console.error(err);
      }
      this.vehicleLayer.clearLayers();
      await this.sleep(3000);
    }
  }

  calculateRemainingTime(eta: number, now: number) {
    const diffMs = Math.max(0, eta - now);
    this.estimatedTime.emit(`${Math.ceil(diffMs / 60000)} min`);
  }

  sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }


}



