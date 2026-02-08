export interface Location {
  address: string;
  lat: number;
  lon: number;
  type?: 'pickup' | 'stop' | 'destination';
  index?: number;
}
