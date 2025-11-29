package com.github.eziocdl.catalog.api.controller;

import com.github.eziocdl.catalog.application.command.dto.CreateProductCommand;
import com.github.eziocdl.catalog.application.command.handler.CreateProductCommandHandler;
import com.github.eziocdl.catalog.application.query.dto.ProductDetailQuery;
import com.github.eziocdl.catalog.application.query.handler.GetProductByIdQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
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
            description = "Receives product data, validates domain rules, persists it in PostgreSQL and returns the generated ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"id\": \"5e4ce4d9-7e65-4523-9783-89002fbf8ea3\"}"))), // <--- O PULO DO GATO AQUI
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., negative price, empty name)"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, UUID>> createProduct(@RequestBody @Valid CreateProductCommand command) {

        UUID productId = createProductCommandHandler.handle(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(Map.of("id", productId));
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