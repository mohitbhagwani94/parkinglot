
package com.example.parking.service;

import com.example.parking.entity.*;
import com.example.parking.exception.InvalidGateException;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.*;
import com.example.parking.strategy.SlotAllocationStrategy;
import com.example.parking.strategy.SlotAllocationStrategyResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.parking.exception.ParkingFullException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {

    private final ParkingSlotRepository slotRepo;
    private final VehicleRepository vehicleRepo;
    private final TicketRepository ticketRepo;
    private final PricingService pricingService;
    private final GateSlotDistanceRepository gateSlotDistanceRepo;
    private final EntryGateRepository entryGateRepo;
    private final SlotAllocationStrategyResolver strategyResolver;

    @PersistenceContext
    private EntityManager em;

    public ParkingService(GateSlotDistanceRepository gateSlotDistanceRepo, ParkingSlotRepository slotRepo, VehicleRepository vehicleRepo, TicketRepository ticketRepo, PricingService pricingService, EntryGateRepository entryGateRepo, SlotAllocationStrategyResolver strategyResolver) {
        this.gateSlotDistanceRepo = gateSlotDistanceRepo;
        this.slotRepo = slotRepo;
        this.vehicleRepo = vehicleRepo;
        this.ticketRepo = ticketRepo;
        this.pricingService = pricingService;
        this.entryGateRepo = entryGateRepo;
        this.strategyResolver = strategyResolver;
    }

    @Transactional
    public Ticket vehicleEntry(String plateNumber, VehicleType type, String ownerName, long gateId) {
        // prevent duplicate active entry for same plate
        var existingVehicle = vehicleRepo.findByPlateNumber(plateNumber).orElse(null);
        if (existingVehicle != null) {
            var existingTicket = ticketRepo.findByVehicleIdAndStatus(existingVehicle.getId(), "ACTIVE");
            if (existingTicket.isPresent()) {
                throw new IllegalStateException("Vehicle already parked with an active ticket.");
            }
        }

        if(!entryGateRepo.existsById(gateId))
            throw new InvalidGateException(gateId);

        Vehicle vehicle = existingVehicle;
        if (vehicle == null) {
            vehicle = Vehicle.builder().plateNumber(plateNumber).vehicleType(type).ownerName(ownerName).build();
            vehicle = vehicleRepo.save(vehicle);
        }

        ParkingSlot chosen = allocateSlotForEntry(type,gateId);

        Ticket ticket = Ticket.builder()
                .vehicleId(vehicle.getId())
                .slotId(chosen.getId())
                .entryTime(LocalDateTime.now())
                .plateNumber(vehicle.getPlateNumber())
                .slotCode(chosen.getSlotCode())
                .paid(false)
                .status("ACTIVE")
                .amount(0.0)
                .build();
        ticket = ticketRepo.save(ticket);
        return ticket;
    }

    public ParkingSlot allocateSlotForEntry(VehicleType type, Long gateId) {
        SlotAllocationStrategy strategy = strategyResolver.getStrategy();
        return strategy.allocateSlot(type, gateId);
    }

    @Transactional
    public ParkingSlot allocateNearestSlotForEntry(VehicleType type, Long gateId) {
        // fetch candidates ordered by distance
        List<ParkingSlot> candidates = gateSlotDistanceRepo.findSlotsByGateAndTypeAndStatusOrdered(
                gateId, type, SlotStatus.FREE);

        if (candidates == null || candidates.isEmpty()) {
            throw new ParkingFullException("No free slots for " + type + " at gate " + gateId);
        }

        // Try each candidate: lock it pessimistically and mark as OCCUPIED.
        for (ParkingSlot candidate : candidates) {
            Long slotId = candidate.getId();
            var maybeSlot = slotRepo.findByIdForUpdate(slotId);
            if (maybeSlot.isEmpty()) continue;
            ParkingSlot slot = maybeSlot.get();

            // double-check it's still free after locking
            if (slot.getStatus() != SlotStatus.FREE) {
                // somebody else took it — try next candidate
                continue;
            }

            slot.setStatus(SlotStatus.OCCUPIED);
            slotRepo.save(slot); // persist change inside transaction
            return slot;
        }

        // No slot could be locked and occupied -> treat as full
        throw new ParkingFullException("No free slots available at the moment (race condition).");
    }

    @Transactional
    public Ticket prepareExit(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Invalid ticket"));
        if (!"ACTIVE".equals(ticket.getStatus())) throw new IllegalStateException("Ticket not active");
        //Vehicle vehicle = vehicleRepo.findById(ticket.getVehicleId()).orElseThrow();
        Vehicle vehicle = vehicleRepo.findByPlateNumber(ticket.getPlateNumber()).orElseThrow();
        //var slot = slotRepo.findById(ticket.getSlotId()).orElseThrow();

        LocalDateTime exit = LocalDateTime.now();
        double amount = pricingService.calculateAmount(vehicle.getVehicleType(), ticket.getEntryTime(), exit);
        ticket.setAmount(amount);
        ticket.setExitTime(exit);
        ticketRepo.save(ticket);
        return ticket;
    }

    @Transactional
    public Ticket finalizeExit(Long ticketId, boolean paymentSuccess) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        if (!"ACTIVE".equals(ticket.getStatus())) throw new IllegalStateException("Ticket not active");
        if (!paymentSuccess) {
            // keep slot occupied
            return ticket;
        }
        // free slot
        var slot = slotRepo.findById(ticket.getSlotId()).orElseThrow();
        slot.setStatus(com.example.parking.model.SlotStatus.FREE);
        slotRepo.save(slot);

        ticket.setPaid(true);
        ticket.setStatus("CLOSED");
        ticketRepo.save(ticket);
        return ticket;
    }
}
