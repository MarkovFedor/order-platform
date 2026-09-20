package org.example.dto;

import lombok.Data;
import org.example.entity.Product;

@Data
public class ProductGet {
    private Long id;
    private String name;
    private Integer price;
    private Integer available;

    public static ProductGet from(Product product) {
        ProductGet productGet = new ProductGet();
        productGet.setName(product.getName());
        productGet.setAvailable(product.getAvailable());
        productGet.setPrice(product.getPrice());
        productGet.setId(product.getId());
        return productGet;
    }
}
