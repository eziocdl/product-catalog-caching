package com.github.eziocdl.catalog.api.controller;

import com.github.eziocdl.catalog.application.command.dto.CreateProductCommand;
import com.github.eziocdl.catalog.application.command.handler.CreateProductCommandHandler;
import com.github.eziocdl.catalog.application.query.dto.ProductDetailQuery;
import com.github.eziocdl.catalog.application.query.handler.GetProductByIdQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product Catalog Management (CQRS Pattern)")
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final GetProductByIdQueryHandler getProductByIdQueryHandler;


    public ProductController(CreateProductCommandHandler createProductCommandHandler,
                             GetProductByIdQueryHandler getProductByIdQueryHandler) {
        this.createProductCommandHandler = createProductCommandHandler;
        this.getProductByIdQueryHandler = getProductByIdQueryHandler;
    }

    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Receives product data, validates domain rules, and persists it in PostgreSQL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., negative price, empty name)"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Void> createProduct(@RequestBody @Valid CreateProductCommand command) {
        createProductCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Retrieves product details. Uses **Redis** for look-aside caching (Hit/Miss strategy).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found in Database")
    })
    public ResponseEntity<ProductDetailQuery> getProductById(@PathVariable UUID id) {
        ProductDetailQuery queryResult = getProductByIdQueryHandler.handle(id);
        return ResponseEntity.ok(queryResult);
    }
}