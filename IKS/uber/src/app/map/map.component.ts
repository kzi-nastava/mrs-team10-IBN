import { AfterViewInit, Component } from '@angular/core';
import * as L from 'leaflet';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import 'leaflet-routing-machine';
import { environment } from '../../environments/environment'; //ZA API-KEY


interface RoutingOptionsWithMarker
  extends L.Routing.RoutingControlOptions {
  createMarker?: () => L.Marker | null;       //da ne bi funkcija za kreiranje rute dodavala deafult-ne ikonice
}


@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})


export class MapComponent implements AfterViewInit {

  private map!: L.Map;
  private routeControl?: L.Routing.Control;

  PinIcon!: L.Icon;
  GreenCarIcon!: L.Icon;
  RedCarIcon!:L.Icon;

  private points: {
    marker: L.Marker;
    latLng: L.LatLng;
  }[] = [];

  constructor(private http: HttpClient) {}


  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
  }

  ngAfterViewInit(): void {
    delete (L.Icon.Default.prototype as any)._getIconUrl;

    this.PinIcon = L.icon({
    iconUrl: 'pointer-pin.svg',
    iconSize: [32, 32],     
    iconAnchor: [16, 32],   
  });

    this.GreenCarIcon = L.icon({
    iconUrl: 'green-car.png',
    iconSize:     [38, 38], 
    iconAnchor:   [22, 94], 
  });

  this.RedCarIcon = L.icon({
      iconUrl: 'red-car.png',
      iconSize:     [38, 38], 
      iconAnchor:   [22, 94], 
  });



    //L.Marker.prototype.options.icon = DefaultIcon;

    this.initMap();
    this.registerOnClick();
    this.search(); 

    L.marker([45.2519, 19.8456], {icon: this.GreenCarIcon}).addTo(this.map);    //demonstracije radi, postavljen slobodan taksista
    L.marker([45.2222, 19.8080], {icon: this.RedCarIcon}).addTo(this.map);

  }

  private addPoint(lat: number, lng: number): void {
    const latLng = L.latLng(lat, lng);
    const pin = L.marker(latLng, {icon: this.PinIcon}).addTo(this.map);

    pin.on('click', () => {
      //this.removePoint();
    });

    this.points.push(  { marker: pin, latLng });
    this.updateRoute();
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

    const waypoints = this.points.map(p => p.latLng);

    if (this.routeControl) {
      this.routeControl.setWaypoints(waypoints);
    } else {
      const options: RoutingOptionsWithMarker = {
      waypoints,
      addWaypoints: false,
      show: false,
      createMarker: () => null,   //da ne bi uzeo defaultne ikonice
      router: L.routing.mapbox(
        environment.apiKey, 
        { profile: 'mapbox/driving'}),
    };

    this.routeControl = L.Routing.control(options).addTo(this.map);

    }
  }


  private registerOnClick(): void {
    this.map.on('click', (e: any) => {
      this.addPoint(e.latlng.lat, e.latlng.lng);
    });
  }


  searchStreet(street: string): Observable<any> {
    return this.http.get(
      'https://nominatim.openstreetmap.org/search?format=json&q=' + street
    );
  }

  search(): void {
    this.searchStreet('Strazilovska 19, Novi Sad').subscribe({
      next: (result) => {
        const lat = +result[0].lat;
        const lon = +result[0].lon;
        this.addPoint(lat, lon);
      },
    });
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`
    );
  }


  clearAll(): void {
    this.points.forEach(p => this.map.removeLayer(p.marker));
    this.points = [];

    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }
  }
}
