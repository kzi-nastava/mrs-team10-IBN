export interface Review{
    id: number,
    userId: number,
    rideId: number,
    driverRating?: number,
    vehicleRating?: number,
    comment?: string
}