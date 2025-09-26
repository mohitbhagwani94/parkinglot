package com.example.parking.repository;

import com.example.parking.entity.EntryGate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryGateRepository extends JpaRepository<EntryGate, Long> {
}
