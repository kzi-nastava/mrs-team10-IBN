export interface DriverDetails {
  accountDTO: {
    email: string;
    type: string;
  };
  createUserDTO: {
    id: number;
    name: string;
    lastName: string;
    homeAddress: string;
    phone: string;
    image?: string;
  };
  vehicleDTO: VehicleDetails;
  uptime: number;
}

export interface VehicleDetails {
  id: number;
  model: string;
  vehicleTypeDTO: {
    id: number;
    name: string;
    price: number;
  };
  plate: string;
  seatNumber: number;
  babySeat: boolean;
  petFriendly: boolean;
}
