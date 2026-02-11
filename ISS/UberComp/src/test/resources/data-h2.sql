INSERT INTO vehicle_type (name, price) VALUES ('STANDARD', 100.0);
INSERT INTO vehicle_type (name, price) VALUES ('LUXURY', 200.0);

INSERT INTO coordinate (lat, lon, address) VALUES (10.0, 10.0, 'Home Address 1');
INSERT INTO coordinate (lat, lon, address) VALUES (20.0, 20.0, 'Home Address 2');
INSERT INTO coordinate (lat, lon, address) VALUES (30.0, 30.0, 'Home Address 3');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2396, 19.8227, 'Bulevar Oslobodjenja 1, Novi Sad');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2551, 19.8451, 'Narodnih heroja 14, Novi Sad');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2400, 19.8230, 'Bulevar Oslobodjenja 5, Novi Sad');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2550, 19.8450, 'Trg Slobode, Novi Sad');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2600, 19.8300, 'Futoski put, Novi Sad');
INSERT INTO coordinate (lat, lon, address) VALUES (45.2500, 19.8400, 'Centar, Novi Sad');

INSERT INTO route (id) VALUES (DEFAULT);

INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 1, 0);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 2, 1);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 3, 2);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('DRIVER', 'VERIFIED', 'driver@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, status, name, last_name, home_address, phone, uptime, total_work_minutes_today)
VALUES (1, 'Driver', 'ONLINE', 'John', 'Doe', 'Bulevar Oslobodjenja 1, Novi Sad', '+381234567890', 0, 0);

INSERT INTO vehicle (driver_id, location_id, vehicle_type_id, model, plate, seat_number, baby_seat, pet_friendly)
VALUES (1, 6, 1, 'Toyota Corolla', 'BG-123-AB', 4, false, false);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('DRIVER', 'VERIFIED', 'driver2@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, status, name, last_name, home_address, phone, uptime, total_work_minutes_today)
VALUES (2, 'Driver', 'ONLINE', 'Mike', 'Smith', 'Narodnih heroja 14, Novi Sad', '+381234567899', 0, 0);

INSERT INTO vehicle (driver_id, location_id, vehicle_type_id, model, plate, seat_number, baby_seat, pet_friendly)
VALUES (2, 5, 1, 'Honda Civic', 'BG-456-CD', 4, false, false);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('DRIVER', 'VERIFIED', 'driver3@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, status, name, last_name, home_address, phone, uptime, total_work_minutes_today)
VALUES (3, 'Driver', 'ONLINE', 'Sarah', 'Johnson', 'Trg Slobode, Novi Sad', '+381234567893', 0, 0);

INSERT INTO vehicle (driver_id, location_id, vehicle_type_id, model, plate, seat_number, baby_seat, pet_friendly)
VALUES (3, 7, 1, 'Volkswagen Golf', 'BG-789-EF', 4, false, false);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('DRIVER', 'VERIFIED', 'driver4@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, status, name, last_name, home_address, phone, uptime, total_work_minutes_today)
VALUES (4, 'Driver', 'ONLINE', 'Tom', 'Williams', 'Futoski put, Novi Sad', '+381234567894', 0, 0);

INSERT INTO vehicle (driver_id, location_id, vehicle_type_id, model, plate, seat_number, baby_seat, pet_friendly)
VALUES (4, 8, 2, 'Mercedes E-Class', 'BG-321-GH', 4, true, true);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('DRIVER', 'VERIFIED', 'driver5@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, status, name, last_name, home_address, phone, uptime, total_work_minutes_today)
VALUES (5, 'Driver', 'ONLINE', 'Emma', 'Davis', 'Centar, Novi Sad', '+381234567895', 0, 0);

INSERT INTO vehicle (driver_id, location_id, vehicle_type_id, model, plate, seat_number, baby_seat, pet_friendly)
VALUES (5, 9, 1, 'Skoda Octavia', 'BG-654-IJ', 4, false, true);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('PASSENGER', 'VERIFIED', 'passenger@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, name, last_name, home_address, phone)
VALUES (6, 'User', 'Jane', 'Smith', 'Narodnih heroja 14, Novi Sad', '+381234567891');

INSERT INTO ride (route_id, driver_id, start, estimated_time_arrival, status, price, distance, babies, pets)
VALUES (1, 1, '2026-02-11 12:00:00', '2026-02-11 12:15:00', 'ONGOING', 150.0, 5.0, false, false);

INSERT INTO ride_passengers (passengers_id, ride_id) VALUES (6, 1);