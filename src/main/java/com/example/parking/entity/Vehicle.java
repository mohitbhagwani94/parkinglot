
package com.example.parking.entity;

import com.example.parking.model.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"plateNumber"})})
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private String ownerName;
}
