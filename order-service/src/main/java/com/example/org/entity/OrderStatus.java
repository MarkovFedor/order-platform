package com.example.org.entity;

import jakarta.persistence.Entity;

public enum OrderStatus {
    PENDING,
    STOCK_RESERVED,
    CONFIRMED,
    CANCELED
}
