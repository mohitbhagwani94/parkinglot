package com.example.parking.controller;

import com.example.parking.entity.PricingRule;
import com.example.parking.repository.PricingRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingRuleRepository repo;

    public PricingController(PricingRuleRepository repo) {
        this.repo = repo;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PricingRule> create(@RequestBody PricingRule rule) {
        System.out.println("===pricing create===");
        return ResponseEntity.ok(repo.save(rule));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PricingRule> update(@PathVariable Long id, @RequestBody PricingRule rule) {
        System.out.println("=== pricing update ===");
        var existing = repo.findById(id).orElseThrow();
        existing.setFreeMinutes(rule.getFreeMinutes());
        existing.setPricePerHour(rule.getPricePerHour());
        existing.setVehicleType(rule.getVehicleType());
        return ResponseEntity.ok(repo.save(existing));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PricingRule>> list() {
        return ResponseEntity.ok(repo.findAll());
    }
}
