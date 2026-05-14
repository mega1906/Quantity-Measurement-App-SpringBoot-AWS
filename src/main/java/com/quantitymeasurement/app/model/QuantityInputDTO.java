package com.quantitymeasurement.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Composite wrapper Data Transfer Object defining the shape of incoming requests.
 * Holds multiple QuantityDTO entities representing the baseline quantity, the 
 * secondary operational quantity, and an optional expected target unit mapping.
 */
@Data
public class QuantityInputDTO {
    @Valid
    @NotNull(message = "First quantity cannot be null")
    private QuantityDTO thisQuantityDTO;

    @Valid
    @NotNull(message = "Second quantity cannot be null")
    private QuantityDTO thatQuantityDTO;


    @Valid
    @Schema(nullable = true)
    private QuantityDTO targetQuantityDTO;
}
