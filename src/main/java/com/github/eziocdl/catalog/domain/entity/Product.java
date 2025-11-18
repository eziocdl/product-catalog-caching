package com.github.eziocdl.catalog.domain.entity;


import com.github.eziocdl.catalog.domain.Exception.DomainValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Product {

    private final UUID id;
    private final String name;
    private final BigDecimal price;

    public Product(UUID id, String name, BigDecimal price) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Price must be greater than zero.");
        }

        //After past create object
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
