INSERT INTO vehicle_type (id, name, price) VALUES (1, 'STANDARD', 120.20);

INSERT INTO coordinate (lat, lon, address) VALUES (10.0, 10.0, 'Home Address 1');
INSERT INTO coordinate (lat, lon, address) VALUES (20.0, 20.0, 'Home Address 2');
INSERT INTO coordinate (lat, lon, address) VALUES (30.0, 30.0, 'Home Address 3');
INSERT INTO coordinate (lat, lon, address) VALUES (40.0, 40.0, 'Home Address 4');
INSERT INTO coordinate (lat, lon, address) VALUES (50.0, 50.0, 'Initial Car Location');

INSERT INTO route VALUES (1);

INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 1, 0);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 2, 1);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (1, 3, 2);

INSERT INTO account (id, account_type, account_status, email, password) VALUES (1, 'DRIVER', 'VERIFIED', 'driver@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');
INSERT INTO app_user (id, account_id, dtype, status, name, last_name, home_address, phone) VALUES (1, 1, 'Driver', 'DRIVING', '','', '', '');
INSERT INTO vehicle (id, driver_id, location_id, vehicle_type_id) VALUES (1, 1, 5, 1);

INSERT INTO account (id, account_type, account_status, email, password) VALUES (2, 'PASSENGER', 'VERIFIED', 'passenger@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');
INSERT INTO app_user (id, account_id, dtype, name, last_name, home_address, phone ) VALUES (2, 2, 'User','','', '', '');
INSERT INTO ride (id, route_id, driver_id, start, estimated_time_arrival, status, price, distance, babies, pets)
VALUES (1, 1, 1, '2026-10-10 12:00:00.000000', '2026-10-10 12:15:00.000000', 'Ongoing', 150.0, 150.0, false, false);
INSERT INTO ride_passengers (passengers_id, ride_id) VALUES (1, 1);