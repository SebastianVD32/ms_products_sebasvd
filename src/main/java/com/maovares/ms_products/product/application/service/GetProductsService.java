package com.maovares.ms_products.product.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.maovares.ms_products.product.application.port.in.GetProductsQuery;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.model.Product;

@Service
public class GetProductsService implements GetProductsQuery {

    private static final Logger log = LoggerFactory.getLogger(GetProductsService.class);
    private final ProductRepository productRepository;

    public GetProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> execute(Pageable pageable) {
        log.info("Getting products with pagination - Page: {}, Size: {}, Sort: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        try {
            log.debug("Executing paginated query in repository");
            Page<Product> products = productRepository.findAll(pageable);
            
            log.info("Successfully retrieved {} products from page {}/{} (total: {} elements)", 
                    products.getContent().size(), 
                    products.getNumber() + 1, 
                    products.getTotalPages(),
                    products.getTotalElements());
            
            return products;
        } catch (Exception e) {
            log.error("Error retrieving products with pagination: {}", e.getMessage(), e);
            throw e;
        }
    }
}
