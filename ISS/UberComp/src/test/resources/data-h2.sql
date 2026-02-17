INSERT INTO vehicle_type (name, price) VALUES ('STANDARD', 100.0);
INSERT INTO vehicle_type (name, price) VALUES ('LUXURY', 200.0);

INSERT INTO coordinate (lat, lon, address) VALUES (10.0, 10.0, 'Home Address 1');
INSERT INTO coordinate (lat, lon, address) VALUES (20.0, 20.0, 'Home Address 2');
INSERT INTO coordinate (lat, lon, address) VALUES (30.0, 30.0, 'Home Address 3');
INSERT INTO coordinate (lat, lon, address) VALUES (40.0, 40.0, 'Home Address 4');
INSERT INTO coordinate (lat, lon, address) VALUES (50.0, 50.0, 'Initial Car Location');
INSERT INTO coordinate (lat, lon, address) VALUES (15.0, 25.0, 'Random Address 1');
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
VALUES (1, 1, '2026-02-11 12:00:00', '2026-02-11 12:15:00', 'Ongoing', 150.0, 5.0, false, false);
INSERT INTO ride (route_id, driver_id, start, estimated_time_arrival, status, price, distance, babies, pets)
VALUES (1, 1, '2026-02-7 12:00:00', '2026-02-7 12:15:00', 'Finished', 150.0, 5.0, false, false);
INSERT INTO ride (route_id, driver_id, start, estimated_time_arrival, status, price, distance, babies, pets)
VALUES (1, 1, '2026-02-19 12:00:00', '2026-02-19 12:15:00', 'Finished', 150.0, 5.0, false, false);

INSERT INTO ride_passengers (passengers_id, ride_id) VALUES (6, 1);
INSERT INTO ride_passengers (passengers_id, ride_id) VALUES (6, 2);
INSERT INTO ride_passengers (passengers_id, ride_id) VALUES (6, 3);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('PASSENGER', 'VERIFIED', 'passenger2@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, name, last_name, home_address, phone)
VALUES (7, 'User', 'Janey', 'Smith', 'Narodnih heroja 14, Novi Sad', '+381234567891');

INSERT INTO route (id) VALUES (DEFAULT);

INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (2, 4, 0);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (2, 5, 1);
INSERT INTO route_stations (route_id, stations_id, station_index) VALUES (2, 6, 2);

INSERT INTO favorite_routes (user_id, route_id) VALUES (7, 2);

INSERT INTO account (account_type, account_status, email, password)
VALUES ('ADMINISTRATOR', 'VERIFIED', 'admin@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');

INSERT INTO app_user (account_id, dtype, name, last_name, home_address, phone)
VALUES (8, 'User', 'Admin', 'Istrator', 'Home Address', '+381234567891');

INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 0, 1, '2026-02-06 15:03:09.153000', '2026-02-06 14:01:29.529000', 1, '2026-02-06 15:00:09.153000', null, 'Panic', 1.325052, '3957a020-bb62-478e-87a9-47c384b2bd25');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 164, 2, '2026-02-06 15:12:54.043119', null,  1, '2026-02-06 15:06:54.043119', null, 'Pending', 1.3665, '38bc1630-4034-4828-980e-bbf61ed5914a');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 0, 3, '2026-02-06 19:40:52.797000', '2026-02-06 18:35:41.319000',  1, '2026-02-06 19:34:52.797000', null, 'Pending', 1.42303, 'a8f3dda3-cece-4fb2-82a0-3ca4ca03e442');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 0, 4, '2026-01-29 14:40:25.236279', '2026-01-29 14:34:16.695000',  2, '2026-01-29 14:32:58.730000', null, 'Panic', null, null);
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 71.40007738317757, 1, '2026-01-30 18:33:10.507224', '2026-01-30 18:29:38.228000',  2, '2026-01-30 18:27:08.482000', null, 'Finished', null, null);
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 445.80503762376236, 2, '2026-02-05 14:40:53.711000', '2026-02-05 14:39:12.741000',  2, '2026-02-05 14:37:58.159000', null, 'Finished', 8.314664, '12345');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 0, 4, '2026-02-01 17:04:58.479788', null,  1, '2026-02-01 16:49:58.479788', 'girl idk', 'CancelledByDriver', null, null);
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 19.54344, 3, '2026-02-10 23:24:09.555000', '2026-02-10 22:17:17.342000',  2, '2026-02-10 23:16:09.555000', null, 'Finished', 1.221465, '0112ea2a-0862-42bb-b6d4-516273134539');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 20.275448275862068, 5, '2026-02-05 22:35:17.662666', '2026-02-05 22:25:37.521425',  2, '2026-02-05 22:24:17.662666', null, 'Finished', 1.6334929999999999, '0798e072-95c6-44bd-b6ec-8d8d3535fb4d');
INSERT INTO ride (babies, pets, price, driver_id, estimated_time_arrival, finish, route_id, start, cancellation_reason, status, distance, tracking_token) VALUES (false, false, 0, 5, '2026-02-05 23:29:02.763316', '2026-02-05 23:26:28.928296',  1, '2026-02-05 23:23:02.763316', null, 'Panic', 1.265409, '0701b06b-32d9-4f2e-bfff-7b26f5a61651');