package com.github.eziocdl.catalog.application.query.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailQuery(
        UUID id,
        String name,
        BigDecimal price
) implements Serializable {
}
