
package com.example.parking.controller;

import com.example.parking.entity.Payment;
import com.example.parking.entity.Ticket;
import com.example.parking.model.VehicleType;
import com.example.parking.service.ParkingService;
import com.example.parking.service.PaymentService;
import com.example.parking.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;
    private final PaymentService paymentService;
    private final PricingService pricingService;

    public ParkingController(ParkingService parkingService, PaymentService paymentService, PricingService pricingService) {
        this.parkingService = parkingService;
        this.paymentService = paymentService;
        this.pricingService = pricingService;
    }

    record EntryRequest(String plateNumber, VehicleType type, String ownerName, int gateId) {}

    @PostMapping("/entry")
    public ResponseEntity<?> entry(@RequestBody EntryRequest req) {
        System.out.println("Entry API req :"+ req);
        var ticket = parkingService.vehicleEntry(req.plateNumber(), req.type(), req.ownerName(), req.gateId());
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/prepareExit/{ticketId}")
    public ResponseEntity<?> prepareExit(@PathVariable Long ticketId) {
        Ticket t = parkingService.prepareExit(ticketId);
        return ResponseEntity.ok(t);
    }

    record PaymentRequest(boolean simulateSuccess) {}

    @PostMapping("/pay/{ticketId}")
    public ResponseEntity<?> pay(@PathVariable Long ticketId, @RequestBody PaymentRequest req) {
        // find ticket amount
        var ticket = parkingService.prepareExit(ticketId);
        Payment p = paymentService.pay(ticketId, ticket.getAmount(), req.simulateSuccess());
        if ("SUCCESS".equals(p.getStatus())) {
            // finalize exit and free slot
            parkingService.finalizeExit(ticketId, true);
            return ResponseEntity.ok(p);
        } else {
            parkingService.finalizeExit(ticketId, false);
            return ResponseEntity.status(400).body(p);
        }
    }
}
