package com.github.eziocdl.catalog.application.command.handler;

import com.github.eziocdl.catalog.application.command.dto.CreateProductCommand;
import com.github.eziocdl.catalog.domain.entity.Product;
import com.github.eziocdl.catalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service // Marca a classe como um serviço Spring
public class CreateProductCommandHandler {

    private final ProductRepository productRepository;

    public CreateProductCommandHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public UUID handle(CreateProductCommand command) {

        Product product = new Product(
                null,
                command.getName(),
                command.getPrice()
        );


        Product savedProduct = productRepository.save(product);

        // EPIC 4.

        return savedProduct.getId();
    }
}