
package com.example.parking.entity;

import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "parking_slot", indexes = {
    @Index(columnList = "floorNumber"),
    @Index(columnList = "status")
})
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private int floorNumber;
    private String slotCode; // e.g., F1-01

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;
}
