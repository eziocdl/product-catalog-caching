package com.github.eziocdl.catalog.application.query.handler;


import com.github.eziocdl.catalog.application.query.dto.ProductDetailQuery;
import com.github.eziocdl.catalog.domain.entity.Product;
import com.github.eziocdl.catalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetProductByIdQueryHandler {

    private final ProductRepository productRepository;

    public GetProductByIdQueryHandler(ProductRepository productRepository ) {
        this.productRepository = productRepository;
    }

    public ProductDetailQuery handle(UUID productID) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not Found"));

        return  new ProductDetailQuery(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
