
-- seed some slots
INSERT INTO PARKING_SLOT (id, floor_number, slot_code, vehicle_type, status) VALUES (1, 1, 'F1-01', 'CAR', 'FREE');
INSERT INTO PARKING_SLOT (id, floor_number, slot_code, vehicle_type, status) VALUES (2, 1, 'F1-02', 'CAR', 'FREE');
INSERT INTO PARKING_SLOT (id, floor_number, slot_code, vehicle_type, status) VALUES (3, 1, 'F1-03', 'BIKE', 'FREE');
INSERT INTO PARKING_SLOT (id, floor_number, slot_code, vehicle_type, status) VALUES (4, 2, 'F2-01', 'TRUCK', 'FREE');

-- Car slots
INSERT INTO parking_slot (floor_number, slot_code, vehicle_type, status) VALUES (1, 'F1-C1', 'CAR', 'FREE');
INSERT INTO parking_slot (floor_number, slot_code, vehicle_type, status) VALUES (1, 'F1-C2', 'CAR', 'FREE');
INSERT INTO parking_slot (floor_number, slot_code, vehicle_type, status) VALUES (2, 'F2-C1', 'CAR', 'FREE');

-- Bike slots
INSERT INTO parking_slot (floor_number, slot_code, vehicle_type, status) VALUES (1, 'F1-B1', 'BIKE', 'FREE');
INSERT INTO parking_slot (floor_number, slot_code, vehicle_type, status) VALUES (1, 'F1-B2', 'BIKE', 'FREE');
