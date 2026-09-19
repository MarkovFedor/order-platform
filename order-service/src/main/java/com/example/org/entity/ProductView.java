package com.example.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_view")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductView {
    @Id
    @Column(name="product_id")
    private Long id;

    @Column(name = "price")
    private Long price;

    @Column(name="name")
    private String name;

    @Column(name="available")
    private Integer available;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
