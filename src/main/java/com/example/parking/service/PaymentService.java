
package com.example.parking.service;

import com.example.parking.entity.Payment;
import com.example.parking.entity.Ticket;
import com.example.parking.repository.PaymentRepository;
import com.example.parking.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final TicketRepository ticketRepo;

    public PaymentService(PaymentRepository paymentRepo, TicketRepository ticketRepo) {
        this.paymentRepo = paymentRepo;
        this.ticketRepo = ticketRepo;
    }

    // Simulated payment via API call - atomic with DB transaction
    @Transactional
    public Payment pay(Long ticketId, double amount, boolean simulateSuccess) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        if (!"ACTIVE".equals(ticket.getStatus())) throw new IllegalStateException("Ticket not active");

        Payment p = Payment.builder()
                .ticketId(ticketId)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .status(simulateSuccess ? "SUCCESS" : "FAILED")
                .build();
        p = paymentRepo.save(p);

        // finalize in ParkingService? For simplicity, PaymentService will not change slot state directly.
        return p;
    }
}
