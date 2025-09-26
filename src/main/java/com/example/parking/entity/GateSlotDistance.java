package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class GateSlotDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gate_id")
    private EntryGate gate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    private ParkingSlot slot;

    // integer distance (lower = closer). You can interpret this as meters/score.
    private int distance;

    public GateSlotDistance(EntryGate gate, ParkingSlot slot, int distance) {
        this.gate = gate;
        this.slot = slot;
        this.distance = distance;
    }

}
