package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ProductCreate;
import org.example.dto.ProductGet;
import org.example.dto.ProductUpdateDto;
import org.example.entity.OutboxEvent;
import org.example.entity.ProcessedEvent;
import org.example.entity.Product;
import org.example.entity.Reservation;
import org.example.events.OrderCreatedEvent;
import org.example.events.ProductAction;
import org.example.events.ProductChangedEvent;
import org.example.repository.OutboxRepository;
import org.example.repository.ProcessedEventRepository;
import org.example.repository.ReservationRepository;
import org.example.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class StockService {
    @Autowired
    private StockRepository stockRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Transactional
    public Long createProduct(ProductCreate request) {
        if(stockRepository.existsByName(request.getName())) {
            return null;
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setAvailable(request.getAvailable());

        stockRepository.save(product);
        publishChange(product, ProductAction.CREATED);
        return product.getId();
    }

    public List<ProductGet> getAllProducts() {
        List<Product> products = stockRepository.findAll();
        List<ProductGet> productGetList = new ArrayList<>();
        for(Product product: products) {
            ProductGet productGet = new ProductGet();
            productGet.setId(product.getId());
            productGet.setAvailable(product.getAvailable());
            productGet.setPrice(product.getPrice());
            productGet.setName(product.getName());
            productGetList.add(productGet);
        }
        return productGetList;
    }

    public ProductGet getProductById(Long id) {
        Optional<Product> product = stockRepository.findById(id);
        if(product.isEmpty()) {
            throw new EntityNotFoundException(String.format("Product with id: %d not found", id));
        }
        ProductGet productGet = new ProductGet();
            productGet.setId(product.get().getId());
            productGet.setAvailable(product.get().getAvailable());
            productGet.setName(product.get().getName());
            productGet.setPrice(product.get().getPrice());
            return productGet;
    }

    public void publishChange(Product product, ProductAction action) {
        /*
        public record ProductChangedEvent(
            UUID eventId,
            Long productId,
            String name,
            Long price,
            Integer available,
            ProductAction action,
            LocalDateTime occurredAt
        ) {}
         */
        ProductChangedEvent event = new ProductChangedEvent(
                UUID.randomUUID(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getAvailable(),
                action,
                LocalDateTime.now()
        );

        String payload = objectMapper.writeValueAsString(event);


        outboxRepository.save(OutboxEvent.of(
                "Product",
                product.getId().toString(),
                "product.changed",
                payload
        ));
    }

    @Transactional
    public ProductGet productUpdate(Long id, ProductUpdateDto request) {
        Product product = stockRepository.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException(String.format("Product with id=%d not found.", id));
        });
        if(request.available() != null) {
            product.setAvailable(request.available());
        }

        if(request.price() != null) {
            product.setPrice(request.price());
        }

        if(request.name() != null) {
            product.setName(request.name());
        }

        stockRepository.save(product);
        publishChange(product, ProductAction.UPDATED);
        return ProductGet.from(product);
    }

    @Transactional
    public void reserve(OrderCreatedEvent event) {
        log.info("Event with id={} received", event.eventId());
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));
        Product product = stockRepository.findByIdForUpdate(event.productId()).orElse(null);
        if(product == null) {
            fail(event);
            return;
        }

        product.setAvailable(product.getAvailable() - event.quanity());
     }

     public void fail(OrderCreatedEvent event) {
        //TODO
     }
}
