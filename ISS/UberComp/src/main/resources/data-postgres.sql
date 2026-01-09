INSERT INTO vehicle_type (id, name, price)
VALUES
    (1, 'STANDARD', 1.0),
    (2, 'VAN', 1.5);

INSERT INTO account (id, email, password, account_type, account_status, blocking_reason)
VALUES
    (1, 'driver@mail.com', 'pass123', 'DRIVER', 'VERIFIED', NULL),
    (2, 'user1@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL),
    (3, 'user2@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL),
    (4, 'admin@mail.com', 'pass123', 'ADMINISTRATOR', 'VERIFIED', NULL);

INSERT INTO app_user
(id, name, last_name, home_address, phone, image, account_id, dtype, uptime, status)
VALUES
    (1, 'Marko', 'Marković', 'Novi Sad', '061111111', NULL, 1, 'Driver', 6, 'ONLINE'),
    (2, 'Ivana', 'Ivić', 'Novi Sad', '062222222', NULL, 2, 'User', NULL, NULL),
    (3, 'Ana', 'Anić', 'Novi Sad', '063333333', NULL, 3, 'User', NULL, NULL),
    (4, 'Bojana', 'Bojanic', 'Novi Sad', '063333333', NULL, 4, 'User', NULL, NULL);


UPDATE account SET user_id=id;

INSERT INTO vehicle
(id, model, plate, seat_number, baby_seat, pet_friendly, vehicle_type_id, driver_id)
VALUES
    (1, 'Corolla', 'NS-123-AA', 4, true, false, 1, 1);

INSERT INTO coordinate (id, lat, lon, address)
VALUES
    (1, 45.2671, 19.8335, 'Bulevar oslobođenja 46'),
    (2, 45.2551, 19.8451, 'Trg slobode 3'),
    (3, 45.2396, 19.8227, 'Futoška 123'),
    (4, 45.2396, 19.8227, 'Futoška 123');

INSERT INTO route (id)
VALUES
    (1),
    (2);

INSERT INTO route_stations (route_id, stations_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (2, 4);

INSERT INTO ride (
    id, route_id, driver_id, babies, pets, price, start, estimated_time_arrival, status, cancellation_reason, panic
) VALUES
      (
          1, 1, 1, true, false, 15.50, '2026-01-04 10:00:00', '2026-01-04 10:25:00', 'Finished', NULL, false
      ),
      (
          2, 2, 1, false, true, 22.00, '2026-01-04 11:00:00', '2026-01-04 11:45:00', 'Finished', 'Passenger cancelled', false
      );

INSERT INTO ride_passengers (ride_id, passengers_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 3);
