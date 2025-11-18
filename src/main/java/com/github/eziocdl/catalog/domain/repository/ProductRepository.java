package com.github.eziocdl.catalog.domain.repository;

import com.github.eziocdl.catalog.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
}
