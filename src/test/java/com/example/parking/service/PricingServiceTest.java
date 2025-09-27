package com.example.parking.service;

import com.example.parking.entity.PricingRule;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.PricingRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @InjectMocks
    private PricingService pricingService;

    @Test
    void amountShouldBeZeroWhenWithinFreeMinutes() {
        // freeMinutes 120, pricePerHour 30
        PricingRule rule = new PricingRule("CAR", 120, 30.0);
        when(pricingRuleRepository.findByVehicleType("CAR")).thenReturn(Optional.of(rule));

        LocalDateTime entry = LocalDateTime.of(2025,1,1,10,0);
        LocalDateTime exit = entry.plusMinutes(100); // within free minutes

        double amount = pricingService.calculateAmount(VehicleType.CAR, entry, exit);
        assertEquals(0.0, amount, 0.001);
    }

    @Test
    void amountShouldRoundUpToNextHourWhenOverFreeMinutes() {
        PricingRule rule = new PricingRule("CAR", 120, 30.0);
        when(pricingRuleRepository.findByVehicleType("CAR")).thenReturn(Optional.of(rule));

        LocalDateTime entry = LocalDateTime.of(2025,1,1,10,0);
        LocalDateTime exit = entry.plusMinutes(121); // 121 minutes -> (121+59)/60 = 3 hours
        double amount = pricingService.calculateAmount(VehicleType.CAR, entry, exit);

        assertEquals(3 * 30.0, amount, 0.001);
    }

    @Test
    void defaultRuleShouldBeUsedWhenRepositoryReturnsEmpty() {
        when(pricingRuleRepository.findByVehicleType("BIKE")).thenReturn(Optional.empty());

        LocalDateTime entry = LocalDateTime.of(2025,1,1,10,0);
        LocalDateTime exit = entry.plusMinutes(200); // > default free for BIKE (120)

        double amount = pricingService.calculateAmount(VehicleType.BIKE, entry, exit);

        // default BIKE rule in service is freeMinutes=120, pricePerHour=10.0
        long minutes = java.time.Duration.between(entry, exit).toMinutes();
        long hours = (minutes + 59) / 60;
        assertEquals(hours * 10.0, amount, 0.001);
    }
}

