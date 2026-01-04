INSERT INTO account (id, email, password, account_type, account_status, blocking_reason)
VALUES
    (1, 'driver@mail.com', 'pass123', 'DRIVER', 'VERIFIED', NULL),
    (2, 'user1@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL),
    (3, 'user2@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL);

INSERT INTO app_user (id, name, last_name, home_address, phone, image, account_id)
VALUES
    (1, 'Marko', 'Marković', 'Sarajevo', '061111111', NULL, 1),
    (2, 'Ivana', 'Ivić', 'Mostar', '062222222', NULL, 2),
    (3, 'Ana', 'Anić', 'Tuzla', '063333333', NULL, 3);

INSERT INTO coordinate (id, lat, lon, address)
VALUES
    (1, 43.8563, 18.4131, 'Baščaršija'),
    (2, 43.8500, 18.4100, 'Marijin Dvor'),
    (3, 43.3438, 17.8078, 'Mostar Centar');

INSERT INTO route (id)
VALUES
    (1),
    (2);

INSERT INTO route_stations (route_id, stations_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 3);

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
    (1, 3);
