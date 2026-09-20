package com.example.payment.outbox;

import com.example.payment.entity.OutboxEvent;
import com.example.payment.repository.OutBoxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutBoxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> batch = outboxRepository
                .findTop100ByPublishedAtIsNullOrderByCreatedAt();

        for (OutboxEvent event : batch) {
            kafkaTemplate.send(topicFor(event), event.getAgregateId(), event.getPayload())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            event.setPublishedAt(LocalDateTime.now());
                        }
                    });
        }
        kafkaTemplate.flush();
    }

    private String topicFor(OutboxEvent event) {
        return event.getEventType();
    }
}