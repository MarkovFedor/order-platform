package org.example.dto;

import lombok.Data;

@Data
public class ProductCreate {
    private String name;
    private Integer price;
    private Integer available;
}
