package com.example.parking.strategy;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.GateSlotDistanceRepository;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.exception.ParkingFullException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component("nearest")
public class NearestSlotStrategy implements SlotAllocationStrategy {

    private final GateSlotDistanceRepository gateSlotDistanceRepo;
    private final ParkingSlotRepository slotRepo;

    public NearestSlotStrategy(GateSlotDistanceRepository gateSlotDistanceRepo,
                               ParkingSlotRepository slotRepo) {
        this.gateSlotDistanceRepo = gateSlotDistanceRepo;
        this.slotRepo = slotRepo;
    }

    @Override
    @Transactional
    public ParkingSlot allocateSlot(VehicleType type, Long gateId) {
        if (gateId == null) {
            throw new IllegalArgumentException("gateId required for nearest strategy");
        }

        List<ParkingSlot> candidates = gateSlotDistanceRepo.findSlotsByGateAndTypeAndStatusOrdered(
                gateId, type, SlotStatus.FREE);

        if (candidates == null || candidates.isEmpty()) {
            throw new ParkingFullException("No free slots for " + type + " at gate " + gateId);
        }

        for (ParkingSlot candidate : candidates) {
            Optional<ParkingSlot> lockedOpt = slotRepo.findByIdForUpdate(candidate.getId());
            if (lockedOpt.isEmpty()) continue;
            ParkingSlot slot = lockedOpt.get();
            if (slot.getStatus() != SlotStatus.FREE) continue;
            slot.setStatus(SlotStatus.OCCUPIED);
            slotRepo.save(slot);
            return slot;
        }

        throw new ParkingFullException("No free slots available (concurrent allocation).");
    }
}
