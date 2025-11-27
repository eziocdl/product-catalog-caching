package com.github.eziocdl.catalog.infrastructure.repository;

import com.github.eziocdl.catalog.domain.entity.Product;
import com.github.eziocdl.catalog.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {
}
