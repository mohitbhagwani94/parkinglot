
package com.example.parking.service;

import com.example.parking.entity.PricingRule;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.PricingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {

    @Autowired
    public PricingRuleRepository pricingRuleRepository;


    public double calculateAmount(VehicleType type, LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();

        PricingRule rule = pricingRuleRepository.findByVehicleType(type.name())
                .orElseGet(() -> {
                    if (type == VehicleType.CAR) return new PricingRule("CAR", 120, 30.0);
                    if (type == VehicleType.BIKE) return new PricingRule("BIKE", 120, 10.0);
                    return new PricingRule("TRUCK", 60, 50.0);
                });

        if (minutes <= 120) return 0.0;
        long hours = (minutes + 59) / 60;
        return hours * rule.getPricePerHour();
    }
}
