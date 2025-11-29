package com.github.eziocdl.catalog.domain.event;

import lombok.Value;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class ProductCreatedEvent {
    UUID id;
    String name;
    BigDecimal price;
    Instant occurredOn;

    public ProductCreatedEvent(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.occurredOn = Instant.now();
    }
}