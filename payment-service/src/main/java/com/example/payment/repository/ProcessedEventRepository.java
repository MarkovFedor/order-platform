package com.example.payment.repository;

import com.example.payment.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    boolean existsById(UUID id);
}
