package com.example.parking.strategy;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.exception.ParkingFullException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirstAvailableStrategyTest {

    @Mock
    private ParkingSlotRepository slotRepo;

    @InjectMocks
    private FirstAvailableStrategy strategy;

    @Test
    void allocateSlot_whenSlotsAvailable_shouldReturnFirstAndMarkOccupied() {
        ParkingSlot slot = ParkingSlot.builder()
                .id(1L)
                .slotCode("F1-01")
                .status(SlotStatus.FREE)
                .vehicleType(VehicleType.CAR)
                .build();

        when(slotRepo.findAndLockFreeSlotsByType(SlotStatus.FREE, VehicleType.CAR))
                .thenReturn(List.of(slot));

        ParkingSlot allocated = strategy.allocateSlot(VehicleType.CAR, 10L);

        assertNotNull(allocated);
        assertEquals(1L, allocated.getId());
        ArgumentCaptor<ParkingSlot> captor = ArgumentCaptor.forClass(ParkingSlot.class);
        verify(slotRepo).save(captor.capture());
        assertEquals(SlotStatus.OCCUPIED, captor.getValue().getStatus());
    }

    @Test
    void allocateSlot_whenNoSlots_shouldThrowParkingFullException() {
        when(slotRepo.findAndLockFreeSlotsByType(SlotStatus.FREE, VehicleType.BIKE))
                .thenReturn(List.of());

        assertThrows(ParkingFullException.class, () ->
                strategy.allocateSlot(VehicleType.BIKE, 1L));
    }
}
