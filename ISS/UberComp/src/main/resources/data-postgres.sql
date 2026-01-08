INSERT INTO account (email, password, account_type, account_status, blocking_reason)
VALUES
    ('driver@mail.com', 'pass123', 'DRIVER', 'VERIFIED', NULL),
    ('user1@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL),
    ('user2@mail.com', 'pass123', 'PASSENGER', 'VERIFIED', NULL);

INSERT INTO app_user (name, last_name, home_address, phone, image, account_id)
VALUES
    ('Marko', 'Marković', 'Novi Sad', '061111111', NULL, 1),
    ('Ivana', 'Ivić', 'Novi Sad', '062222222', NULL, 2),
    ('Ana', 'Anić', 'Novi Sad', '063333333', NULL, 3);

UPDATE account SET user_id=id;

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
