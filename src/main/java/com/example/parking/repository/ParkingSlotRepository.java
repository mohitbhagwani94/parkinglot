
package com.example.parking.repository;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    // Find nearest (lowest floor then lowest id) free slot for vehicle type
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ParkingSlot s where s.status = :status and s.vehicleType = :type order by s.floorNumber asc, s.id asc")
    List<ParkingSlot> findAndLockFreeSlotsByType(@Param("status") SlotStatus status, @Param("type") VehicleType type);
}
