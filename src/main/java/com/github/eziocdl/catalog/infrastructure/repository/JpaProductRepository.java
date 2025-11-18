package com.github.eziocdl.catalog.infrastructure.repository;


// Adapter

import com.github.eziocdl.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID>, com.github.eziocdl.catalog.domain.repository.ProductRepository {







}
