package com.github.eziocdl.catalog.domain.entity;

import com.github.eziocdl.catalog.domain.Exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID; // <--- Importação necessária
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    // RB001 : Price > 0
    @Test
    void shouldThrowExceptionWhenPriceIsZero() {
        // AAA: Arrange
        UUID validId = UUID.randomUUID(); // <--- Variável declarada
        String validName = "Test Product";
        BigDecimal zeroPrice = BigDecimal.ZERO;

        assertThrows(DomainValidationException.class, () -> {
            // Este teste DEVE FALHAR porque a validação de preço ainda não existe no construtor de Product.
            new Product(validId, validName, zeroPrice);
        });
    }
}