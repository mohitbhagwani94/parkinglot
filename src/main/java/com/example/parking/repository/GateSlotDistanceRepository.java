package com.example.parking.repository;

import com.example.parking.entity.GateSlotDistance;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GateSlotDistanceRepository extends JpaRepository<GateSlotDistance, Long> {

    // Return list of slots (via gsd.slot) which are FREE and matching vehicle type, ordered by distance ASC
    @Query("SELECT gsd.slot FROM GateSlotDistance gsd " +
            "WHERE gsd.gate.id = :gateId " +
            "AND gsd.slot.vehicleType = :type " +
            "AND gsd.slot.status = :status " +
            "ORDER BY gsd.distance ASC")
    List<ParkingSlot> findSlotsByGateAndTypeAndStatusOrdered(@Param("gateId") Long gateId,
                                                             @Param("type") VehicleType type,
                                                             @Param("status") SlotStatus status);
}
