
package com.example.parking.controller;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.model.SlotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.ParkingSlotRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ParkingSlotRepository slotRepo;

    public AdminController(ParkingSlotRepository slotRepo) {
        this.slotRepo = slotRepo;
    }

    record CreateSlotRequest(int floorNumber, String slotCode, VehicleType vehicleType) {}

    @PostMapping("/slot")
    public ResponseEntity<?> addSlot(@RequestBody CreateSlotRequest req) {
        ParkingSlot s = ParkingSlot.builder()
                .floorNumber(req.floorNumber)
                .slotCode(req.slotCode)
                .vehicleType(req.vehicleType)
                .status(SlotStatus.FREE)
                .build();
        s = slotRepo.save(s);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<ParkingSlot>> list() {
        return ResponseEntity.ok(slotRepo.findAll());
    }

    @DeleteMapping("/slot/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        slotRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
