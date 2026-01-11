import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter,
  Signal,
} from '@angular/core';
import * as L from 'leaflet';
import { Observable, map } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import 'leaflet-routing-machine';
import { environment } from '../../../environments/environment';
import { Location } from '../../model/location.model';
import { output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Station } from '../../model/ride-history.model';
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
export class TrackingMapComponent implements AfterViewInit, OnChanges{
  @Input() stations: Station[] = [];
  @Input() interactive: boolean = true;
  
  estimatedTime = output<string>();
  currentLocation = output<TrackingData>();

  private passedCount = 0
  passingOutput = output<number>();
  
  private map!: L.Map;
  private routeControl?: L.Routing.Control;

  PinIcon!: L.Icon;
  PickupIcon!: L.Icon;
  DestinationIcon!: L.Icon;
  GreenCarIcon!: L.Icon;
  RedCarIcon!: L.Icon;
  CurrentLocationIcon!: L.Icon;
  
  private points: {
    marker: L.Marker;
    latLng: L.LatLng;
    address?: string;
  }[] = [];
  
  private currentLocationMarker!: L.Marker;

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

    this.CurrentLocationIcon = L.icon({
      iconUrl: 'current-loc.svg',
      iconSize: [16, 16],
      iconAnchor: [8, 8],
    })

    this.initMap();

    
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.stations && this.stations.length > 0) {
      this.updateLocationsFromInput().then(() => {
        this.currentLocationMarker = L.marker(this.points[0].latLng, {icon: this.CurrentLocationIcon}).addTo(this.map);
      });
    }
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.242, 19.8227],
      zoom: 12.75,
      zoomSnap:0.25,
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

    this.map.on('click', (e) => {
      this.currentLocationMarker.setLatLng(e.latlng);
      if (e.latlng.distanceTo(this.points[this.passedCount].latLng) < 50){
        console.log("Station reached!")
        ++this.passedCount;
        this.passingOutput.emit(this.passedCount);
        console.log(this.points)
      }
      this.getTrackingData(e.latlng).subscribe({
        next: (response) => {
          this.currentLocation.emit(response)
        }
      })
    })
  }

  private async updateLocationsFromInput(): Promise<void> {
    if (!this.stations || this.stations.length === 0) {
      this.clearAll();
      return;
    }

    this.isUpdatingFromParent = true;
    this.clearAll();

    for (const location of this.stations) {
      await this.addLocationFromAddress(location);
    }

    this.updateRoute();
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
    fromParent = false,
    shouldUpdateRoute = true 
  ): void {
    const latLng = L.latLng(lat, lng);
    const pin = L.marker(latLng, { icon, title }).addTo(this.map);

    if (title) {
      pin.bindPopup(title);
    }

    this.points.push({ marker: pin, latLng, address: title });
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
        this.estimatedTime.emit(Math.round((summary.totalTime % 3600) / 60) + " minutes");
      });
    }
  }

  getTrackingData(position: L.LatLng): Observable<TrackingData> {
    var latRound: number = Number(position.lat.toFixed(7))
    var lonRound: number = Number(position.lng.toFixed(7))
    return this.reverseSearch(latRound, lonRound).pipe(
      map((response) => ({
        lat: latRound,
        lon: lonRound,
        address: response.address.road + ' ' + response.address.house_number
      }))
    );
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