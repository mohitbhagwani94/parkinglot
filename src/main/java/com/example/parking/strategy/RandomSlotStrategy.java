package com.example.parking.strategy;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.exception.ParkingFullException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component("random")
public class RandomSlotStrategy implements SlotAllocationStrategy {

    private final ParkingSlotRepository slotRepo;

    public RandomSlotStrategy(ParkingSlotRepository slotRepo) {
        this.slotRepo = slotRepo;
    }

    @Override
    @Transactional
    public ParkingSlot allocateSlot(VehicleType type, Long gateId) {
        // fetch free slots (no lock) — then attempt to lock by id
        List<ParkingSlot> free = slotRepo.findByVehicleTypeAndStatus(type, SlotStatus.FREE);
        if (free == null || free.isEmpty()) throw new ParkingFullException("No free slots for " + type);

        // shuffle and attempt to lock & occupy
        Collections.shuffle(free);
        for (ParkingSlot candidate : free) {
            Optional<ParkingSlot> locked = slotRepo.findByIdForUpdate(candidate.getId());
            if (locked.isEmpty()) continue;
            ParkingSlot slot = locked.get();
            if (slot.getStatus() != SlotStatus.FREE) continue;
            slot.setStatus(SlotStatus.OCCUPIED);
            slotRepo.save(slot);
            return slot;
        }
        throw new ParkingFullException("No free slots available (concurrent allocation).");
    }
}
