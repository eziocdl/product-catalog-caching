package com.github.eziocdl.catalog.domain.entity;

import com.github.eziocdl.catalog.domain.Exception.DomainValidateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    // RB001 : Price > 0

    @Test
    void ShouldThrowExceptionWhenPriceIsZero() {
        String validName = "Test Product";
        BigDecimal zeroPrice = BigDecimal.ZERO;

        assertThrows(DomainValidateException.class, () -> {
            // A classe Product e a exceção DomainValidationException ainda não existem.
            new Product(validName, zeroPrice);
        });


    }
}
