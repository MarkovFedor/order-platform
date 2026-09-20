package org.example.dto;

import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

public record ProductUpdateDto(
    String name,
    Integer available,
    Integer price
){}

