package org.example.controller;

import org.example.dto.ProductCreate;
import org.example.dto.ProductGet;
import org.example.dto.ProductUpdateDto;
import org.example.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body("Healthy");
    }

    @PostMapping("/create")
    public Long createProduct(@RequestBody ProductCreate request) {
        return stockService.createProduct(request);
    }

    @GetMapping
    public List<ProductGet> getAllProducts() {
        return stockService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductGet getProduct(@PathVariable Long id) {
        return stockService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductGet updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDto request) {
        return stockService.productUpdate(id, request);
    }
}
