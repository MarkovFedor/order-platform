package com.example.org.service;

import com.example.events.ProductAction;
import com.example.events.ProductChangedEvent;
import com.example.org.dto.ProductViewResponseDto;
import com.example.org.entity.ProcessedEvent;
import com.example.org.entity.ProductView;
import com.example.org.repository.ProcessedEventRepository;
import com.example.org.repository.ProductViewRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductViewService {

    @Autowired
    private ProductViewRepository productViewRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Transactional
    public void apply(ProductChangedEvent event) {
        log.info("Event with id={} received", event.eventId());
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));
        if(event.action() == ProductAction.DELETED) {
            productViewRepository.deleteById(event.productId());
            return;
        }

        Optional<ProductView> existing = productViewRepository.findById(event.productId());

        ProductView view;
        if (existing.isPresent()) {
            view = existing.get();
        } else {
            view = new ProductView();
            view.setId(event.productId());
        }

        view.setName(event.name());
        view.setPrice(event.price());
        view.setAvailable(event.available());
        view.setUpdatedAt(LocalDateTime.now());

        if (existing.isEmpty()) {
            productViewRepository.save(view);
            log.info("Product view with id={} saved", view.getId());
        }
    }

    public List<ProductViewResponseDto> getAllProducts() {
        List<ProductView> products = productViewRepository.findAll();
        return products.stream()
                .map(ProductViewResponseDto::from)
                .toList();
    }
}
