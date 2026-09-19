package com.example.org.repository;

import com.example.org.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxRepository extends JpaRepository<OutboxEvent, Long> {
    public List<OutboxEvent> findTop100ByPublishedAtIsNullOrderByCreatedAt();
}
