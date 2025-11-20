package com.github.eziocdl.catalog.api.controller;

import com.github.eziocdl.catalog.application.command.handler.CreateProductCommandHandler;
import com.github.eziocdl.catalog.application.query.dto.ProductDetailQuery;
import com.github.eziocdl.catalog.application.query.handler.GetProductByIdQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // <-- Novo Import

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final GetProductByIdQueryHandler getProductByIdQueryHandler; // <-- Adicionar

    // Ajuste no Construtor para injeção de ambos os Handlers (Command e Query)
    public ProductController(
            CreateProductCommandHandler createProductCommandHandler,
            GetProductByIdQueryHandler getProductByIdQueryHandler
    ) {
        this.createProductCommandHandler = createProductCommandHandler;
        this.getProductByIdQueryHandler = getProductByIdQueryHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailQuery> getProductById(@PathVariable UUID id) {
        ProductDetailQuery queryResult = getProductByIdQueryHandler.handle(id);
        return ResponseEntity.ok(queryResult);

    }
}
