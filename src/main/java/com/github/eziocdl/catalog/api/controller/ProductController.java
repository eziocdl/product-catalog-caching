package com.github.eziocdl.catalog.api.controller;

import com.github.eziocdl.catalog.application.command.dto.CreateProductCommand;
import com.github.eziocdl.catalog.application.command.handler.CreateProductCommandHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;

    // Injection to handler (CQRS)

    public ProductController(CreateProductCommandHandler createProductCommandHandler) {
        this.createProductCommandHandler = createProductCommandHandler;
    }

    @PostMapping
    public ResponseEntity<UUID> createProduct(@RequestBody @Valid CreateProductCommand command) {
        UUID productId = createProductCommandHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

}
