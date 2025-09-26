
package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;
    private Long slotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean paid;
    private double amount;
    private String plateNumber;
    private String slotCode;
    private String status; // ACTIVE, CLOSED
}
