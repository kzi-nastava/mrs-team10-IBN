export interface Review{
    id: number,
    rideId: number,
    driverRating?: number,
    vehicleRating?: number,
    comment?: string
}