package com.github.eziocdl.catalog.domain.entity;

import com.github.eziocdl.catalog.domain.Exception.DomainValidationException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id; // <-- CORRIGIDO
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "products")
public class Product {

    @Id // <-- Agora aponta para jakarta.persistence.Id
    private final UUID id;
    private final String name;
    private final BigDecimal price;

    // Construtor obrigatório do JPA (protected para evitar uso fora do Framework)
    protected Product() {
        // Inicializa campos final para satisfazer o compilador Java
        this.id = null;
        this.name = null;
        this.price = null;
    }

    public Product(UUID id, String name, BigDecimal price) {
        // ... (validações de preço e nome aqui)
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Price must be greater than zero.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("Product name cannot be null or empty.");
        }

        // Se passar nas regras, o objeto é criado (Imutabilidade)
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.price = price;
    }
}