INSERT INTO vehicle_type (name, price)
VALUES
    ('STANDARD', 120.20),
    ('VAN', 222.22),
    ('LUXURY', 333.33);

INSERT INTO vehicle (model, plate, seat_number, baby_seat, pet_friendly, vehicle_type_id, driver_id)
VALUES
    ('Corolla', 'NS-123-AA', 4, TRUE, FALSE, 1, 1);

INSERT INTO coordinate (lat, lon, address)
VALUES
    (45.2671, 19.8335, 'Bulevar oslobođenja 46'),
    (45.2551, 19.8451, 'Trg slobode 3'),
    (45.2396, 19.8227, 'Futoška 123'),
    (45.2396, 19.8227, 'Futoška 123');

INSERT INTO route DEFAULT VALUES;
INSERT INTO route DEFAULT VALUES;

INSERT INTO route_stations (route_id, stations_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (2, 4);

INSERT INTO ride (route_id, driver_id, babies, pets, price, start, estimated_time_arrival, status, cancellation_reason, panic)
VALUES
    (1, 4, TRUE, FALSE, 15.50, '2026-01-04 10:00:00', '2026-01-04 10:25:00', 'Finished', NULL, FALSE),
    (2, 4, FALSE, TRUE, 22.00, '2026-01-04 11:00:00', '2026-01-04 11:45:00', 'Finished', 'Passenger cancelled', FALSE);

INSERT INTO ride_passengers (ride_id, passengers_id)
VALUES
    (1, 3),
    (1, 2),
    (2, 3);

