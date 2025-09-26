package com.example.parking.strategy;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.VehicleType;

public interface SlotAllocationStrategy {
    /**
     * Allocate a slot for given vehicle type coming from gateId.
     * gateId may be null for strategies that don't use gate proximity.
     *
     * @param type   vehicle type (CAR/BIKE/TRUCK)
     * @param gateId entry gate id (nullable)
     * @return allocated ParkingSlot (persisted & marked OCCUPIED)
     * @throws com.example.parking.exception.ParkingFullException if no slot available
     */
    ParkingSlot allocateSlot(VehicleType type, Long gateId);
}
