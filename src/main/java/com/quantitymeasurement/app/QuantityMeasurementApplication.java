package com.quantitymeasurement.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Quantity Measurement Spring Boot application.
 * Initializes the application context and sets up Swagger/OpenAPI configurations.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Quantity Measurement API",
        version = "1.0.0",
        description = "REST API for quantity measurements with support for multiple unit types"
    )
)
public class QuantityMeasurementApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApplication.class, args);
    }
}
