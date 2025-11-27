package com.github.eziocdl.catalog.domain.entity;

import com.github.eziocdl.catalog.domain.Exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    void shouldThrowExceptionWhenPriceIsZero() {
        UUID validId = UUID.randomUUID();
        String validName = "Test Product";
        BigDecimal zeroPrice = BigDecimal.ZERO;

        assertThrows(DomainValidationException.class, () ->
            new Product(validId, validName, zeroPrice)
        );
    }
}