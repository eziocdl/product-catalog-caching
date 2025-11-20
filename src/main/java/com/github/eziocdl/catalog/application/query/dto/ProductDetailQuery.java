package com.github.eziocdl.catalog.application.query.dto;


import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class ProductDetailQuery {

    UUID id;
    String name;
    BigDecimal price;
}
