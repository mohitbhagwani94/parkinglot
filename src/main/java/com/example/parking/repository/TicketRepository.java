
package com.example.parking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.parking.entity.Ticket;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByVehicleIdAndStatus(Long vehicleId, String status);
}
