
package com.example.parking;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
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
}
