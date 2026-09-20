package com.example.org.dto;

import com.example.org.entity.ProductView;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductViewResponseDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer available;
    private LocalDateTime updatedAt;

    public static ProductViewResponseDto from(ProductView product) {
        ProductViewResponseDto responseDto = new ProductViewResponseDto();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setAvailable(product.getAvailable());
        responseDto.setPrice(product.getPrice());
        responseDto.setUpdatedAt(product.getUpdatedAt());
        return responseDto;
    }
}
