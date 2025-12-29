export interface User{
    id: number,
    name: string,
    email: string,
    role: 'passenger' | 'driver' | 'administrator'
    phoneNumber: string
}