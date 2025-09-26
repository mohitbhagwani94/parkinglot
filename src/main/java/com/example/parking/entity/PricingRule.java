package com.example.parking.entity;

import jakarta.persistence.*;
import java.time.Duration;

@Entity
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public long getFreeMinutes() { return freeMinutes; }
    public void setFreeMinutes(long freeMinutes) { this.freeMinutes = freeMinutes; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
}
