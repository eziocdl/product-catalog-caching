package com.github.eziocdl.catalog.application.command.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class CreateProductCommand {
    @NotBlank(message = "Product name cannot be empty.")
    String name;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    BigDecimal price;
}
