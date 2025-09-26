
package com.example.parking.service;

import com.example.parking.entity.*;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.*;
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

    @PersistenceContext
    private EntityManager em;

    public ParkingService(ParkingSlotRepository slotRepo, VehicleRepository vehicleRepo, TicketRepository ticketRepo, PricingService pricingService) {
        this.slotRepo = slotRepo;
        this.vehicleRepo = vehicleRepo;
        this.ticketRepo = ticketRepo;
        this.pricingService = pricingService;
    }

    @Transactional
    public Ticket vehicleEntry(String plateNumber, VehicleType type, String ownerName) {
        // prevent duplicate active entry for same plate
        var existingVehicle = vehicleRepo.findByPlateNumber(plateNumber).orElse(null);
        if (existingVehicle != null) {
            var existingTicket = ticketRepo.findByVehicleIdAndStatus(existingVehicle.getId(), "ACTIVE");
            if (existingTicket.isPresent()) {
                throw new IllegalStateException("Vehicle already parked with an active ticket.");
            }
        }

        Vehicle vehicle = existingVehicle;
        if (vehicle == null) {
            vehicle = Vehicle.builder().plateNumber(plateNumber).vehicleType(type).ownerName(ownerName).build();
            vehicle = vehicleRepo.save(vehicle);
        }

        // find and lock free slot
        List<ParkingSlot> candidates = slotRepo.findAndLockFreeSlotsByType(SlotStatus.FREE, type);
        if (candidates.isEmpty()) {
            throw new IllegalStateException("Parking Full for vehicle type: " + type);
        }

        ParkingSlot chosen = candidates.get(0);
        // mark occupied
        chosen.setStatus(SlotStatus.OCCUPIED);
        slotRepo.save(chosen);

        Ticket ticket = Ticket.builder()
                .vehicleId(vehicle.getId())
                .slotId(chosen.getId())
                .entryTime(LocalDateTime.now())
                .paid(false)
                .status("ACTIVE")
                .amount(0.0)
                .build();
        ticket = ticketRepo.save(ticket);
        return ticket;
    }

    @Transactional
    public Ticket prepareExit(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Invalid ticket"));
        if (!"ACTIVE".equals(ticket.getStatus())) throw new IllegalStateException("Ticket not active");
        Vehicle vehicle = vehicleRepo.findById(ticket.getVehicleId()).orElseThrow();
        var slot = slotRepo.findById(ticket.getSlotId()).orElseThrow();

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
