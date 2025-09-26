
package com.example.parking;

import com.example.parking.entity.EntryGate;
import com.example.parking.entity.GateSlotDistance;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.EntryGateRepository;
import com.example.parking.repository.GateSlotDistanceRepository;
import com.example.parking.repository.ParkingSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ParkingLotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingLotApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(ParkingSlotRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new ParkingSlot(null, 1, "F1-C1", VehicleType.CAR, SlotStatus.FREE));
                repo.save(new ParkingSlot(null, 1, "F1-C2", VehicleType.CAR, SlotStatus.FREE));
                repo.save(new ParkingSlot(null, 2, "F2-C1", VehicleType.CAR, SlotStatus.FREE));
                repo.save(new ParkingSlot(null, 1, "F1-B1", VehicleType.BIKE, SlotStatus.FREE));
                repo.save(new ParkingSlot(null, 1, "F1-B2", VehicleType.BIKE, SlotStatus.FREE));
            }
        };
    }

    @Bean
    public CommandLineRunner seedPricingRules(com.example.parking.repository.PricingRuleRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new com.example.parking.entity.PricingRule("CAR", 120, 30.0));
                repo.save(new com.example.parking.entity.PricingRule("BIKE", 120, 10.0));
                repo.save(new com.example.parking.entity.PricingRule("TRUCK", 60, 50.0));
            }
        };
    }

    @Bean
    CommandLineRunner seedData(
            EntryGateRepository gateRepo,
            ParkingSlotRepository slotRepo,
            GateSlotDistanceRepository mappingRepo) {
        return args -> {
            if (gateRepo.count() == 0) {
                // Create gates
                EntryGate gateA = gateRepo.save(new EntryGate("Gate A"));
                EntryGate gateB = gateRepo.save(new EntryGate("Gate B"));

                // Assuming parking slots already exist (ids 1..N)
                ParkingSlot slot1 = slotRepo.findById(1L).orElse(null);
                ParkingSlot slot2 = slotRepo.findById(2L).orElse(null);
                ParkingSlot slot3 = slotRepo.findById(3L).orElse(null);

                if (slot1 != null) {
                    mappingRepo.save(new GateSlotDistance(gateA, slot1, 10));
                }
                if (slot2 != null) {
                    mappingRepo.save(new GateSlotDistance(gateA, slot2, 20));
                }
                if (slot3 != null) {
                    mappingRepo.save(new GateSlotDistance(gateB, slot3, 5));
                }
            }
        };
    }

}
