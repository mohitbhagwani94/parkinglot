
package com.example.parking.service;

import com.example.parking.model.VehicleType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {

    // Simple pricing: first 2 hours free, then hourly rates per vehicle type
    public double calculateAmount(VehicleType type, LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= 120) return 0.0;
        long hours = (minutes - 120 + 59) / 60; // ceil
        double ratePerHour = switch (type) {
            case BIKE -> 10.0;
            case CAR -> 30.0;
            case TRUCK -> 50.0;
        };
        return hours * ratePerHour;
    }
}
