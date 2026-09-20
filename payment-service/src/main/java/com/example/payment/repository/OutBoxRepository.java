package com.example.payment.repository;

import com.example.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxRepository extends JpaRepository<OutboxEvent, Long> {
    public List<OutboxEvent> findTop100ByPublishedAtIsNullOrderByCreatedAt();
}
