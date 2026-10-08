package com.maovares.ms_products.product.infraestructure.messaging.azure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.maovares.ms_products.product.application.port.out.ProductEventPublisher;
import com.maovares.ms_products.product.domain.model.Product;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class AzureQueueProductEventPublisher implements ProductEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(AzureQueueProductEventPublisher.class);

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;

    public AzureQueueProductEventPublisher(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.queue-name}") String queueName,
            ObjectMapper objectMapper) {

        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();

        this.objectMapper = objectMapper;
    }

    @Override
    public void publishProductCreated(Product product) {

        try {

            Map<String, Object> event = new HashMap<>();

            event.put("orderId", product.getId());
            event.put("customerEmail", "sevadi01@gmail.com");
            event.put("customerName", "Sebastian");
            event.put("total", product.getPrice());
            event.put("productTitle", product.getTitle());

            String json = objectMapper.writeValueAsString(event);

            String encodedMessage = Base64.getEncoder()
                    .encodeToString(
                            json.getBytes(StandardCharsets.UTF_8)
                    );

            queueClient.sendMessage(encodedMessage);

            log.info(
                    "ProductCreated event sent to Azure Queue - Product ID: {}",
                    product.getId()
            );

        } catch (JacksonException e) {

            log.error(
                    "Error serializing ProductCreated event: {}",
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(e);
        }
    }
}