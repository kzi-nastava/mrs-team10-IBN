INSERT INTO vehicle_type (id, name, price) VALUES (1, 'STANDARD', 120.20);

INSERT INTO coordinate VALUES (1, 10.0, 10.0, "Home Address 1");
INSERT INTO coordinate VALUES (2, 20.0, 20.0, "Home Address 2");
INSERT INTO coordinate VALUES (3, 30.0, 30.0, "Home Address 3");
INSERT INTO coordinate VALUES (4, 40.0, 40.0, "Home Address 4");
INSERT INTO coordinate VALUES (5, 50.0, 50.0, "Initial Car Location");

INSERT INTO route VALUES (1);

INSERT INTO route_stations VALUES (1, 1);
INSERT INTO route_stations VALUES (1, 1);
INSERT INTO route_stations VALUES (1, 1);

INSERT INTO account (id, account_type, email, password) VALUES (1, 'VERIFIED', 'driver@mail.com', '$2a$10$8xtcyxlP5VmjAw620SF70uLMvV6PhwTNydwZe1OeqJXoXGzgoqjRq');
INSERT INTO app_user (id, account_id, status) VALUES (1, 1, 'DRIVING');
INSERT INTO vehicle (id, driver_id, location_id, vehicle_type_id) VALUES (1, 1, 5, 1);

INSERT INTO account (id, account_type, email) VALUES (2, 'VERIFIED', 'passenger@mail.com');
INSERT INTO app_user (id, account_id) VALUES (2, 2);
INSERT INTO ride (id, route_id, driver_id, start, estimated_time_arrival, status, price, distance)
VALUES (1, 1, 1, '2026-10-10 12:00:00.000000', '2026-10-10 12:15:00.000000', 'Ongoing', 150.0, 150.0);
INSERT INTO ride_passengers VALUES (1, 1);