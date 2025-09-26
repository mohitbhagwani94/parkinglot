package com.example.parking.strategy;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.exception.ParkingFullException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("first")
public class FirstAvailableStrategy implements SlotAllocationStrategy {

    private final ParkingSlotRepository slotRepo;

    public FirstAvailableStrategy(ParkingSlotRepository slotRepo) {
        this.slotRepo = slotRepo;
    }

    @Override
    @Transactional
    public ParkingSlot allocateSlot(VehicleType type, Long gateId) {
        // repository method that returns locked rows (annotated @Lock(PESSIMISTIC_WRITE))
        List<ParkingSlot> candidates = slotRepo.findAndLockFreeSlotsByType(SlotStatus.FREE, type);
        if (candidates == null || candidates.isEmpty()) {
            throw new ParkingFullException("No free slots for " + type);
        }
        ParkingSlot slot = candidates.get(0); // first available
        slot.setStatus(SlotStatus.OCCUPIED);
        slotRepo.save(slot);
        return slot;
    }
}
