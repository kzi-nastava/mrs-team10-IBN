export interface Location {
  address: string;
  type: 'pickup' | 'stop' | 'destination';
  index?: number;
}