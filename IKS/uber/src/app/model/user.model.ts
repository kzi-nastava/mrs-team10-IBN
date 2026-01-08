export interface User {
  id: number;
  name: string;
  lastName: string;
  role: 'passenger' | 'driver' | 'administrator';
  phone: string;
  image: string;
  address: string;
  email: string;
}
