package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;

@Entity
@Data
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., BIKE, CAR, TRUCK
    private String vehicleType;

    // number of free minutes included (e.g., 120)
    private long freeMinutes;

    // price per hour after free time
    private double pricePerHour;

    public PricingRule() {}

    public PricingRule(String vehicleType, long freeMinutes, double pricePerHour) {
        System.out.println("=== PricingRule===");
        this.vehicleType = vehicleType;
        this.freeMinutes = freeMinutes;
        this.pricePerHour = pricePerHour;
    }
}
