package com.maovares.ms_products.product.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.maovares.ms_products.product.application.port.in.GetProductQuery;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.exception.ProductNotFoundException;
import com.maovares.ms_products.product.domain.model.Product;

@Service
public class GetProductService implements GetProductQuery {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);
    private final ProductRepository productRepository;

    public GetProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(String id) {
        log.info("Getting product by ID: {}", id);
        
        try {
            log.debug("Searching for product in repository - ID: {}", id);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));
            
            log.info("Product found successfully - ID: {}, Title: {}", product.getId(), product.getTitle());
            return product;
        } catch (ProductNotFoundException e) {
            log.warn("Product not found - ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
