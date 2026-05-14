package com.quantitymeasurement.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.logging.Logger;

/**
 * Data Transfer Object capturing a single quantity measurement (value and
 * unit).
 * Provides programmatic enumeration definitions for scaling constants and
 * contains
 * built-in validation attributes to intercept malformed inbound requests.
 */
@Data
@NoArgsConstructor
@Schema(description = "A quantity with a value and unit")
public class QuantityDTO {

    private static final Logger logger = Logger.getLogger(QuantityDTO.class.getName());

    public enum LengthUnit implements MeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS
    }

    public enum VolumeUnit implements MeasurableUnit {
        LITRE, MILLILITER, GALLON
    }

    public enum WeightUnit implements MeasurableUnit {
        MILLIGRAM, GRAM, KILOGRAM, POUND, TONNE
    }

    public enum TemperatureUnit implements MeasurableUnit {
        CELSIUS, FAHRENHEIT
    }

    @NotNull(message = "Value cannot be empty")
    @Schema(example = "1.0")
    public Double value;

    @NotEmpty(message = "Unit cannot be null")
    @Schema(example = "FEET", allowableValues = { "FEET", "INCHES", "YARDS", "CENTIMETERS", "LITRE", "MILLILITER",
            "GALLON", "MILLIGRAM", "GRAM", "KILOGRAM", "POUND", "TONNE", "CELSIUS", "FAHRENHEIT" })
    public String unit;

    @NotEmpty(message = "Measurement type cannot be null")
    @Pattern(regexp = "LengthUnit|VolumeUnit|WeightUnit|TemperatureUnit", message = "Measurement type must be one of: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit")
    @Schema(example = "LengthUnit", allowableValues = { "LengthUnit", "VolumeUnit", "WeightUnit", "TemperatureUnit" })
    public String measurementType;

    @AssertTrue(message = "Unit must be valid for the specified measurement type")
    public boolean isValidUnit() {
        try {
            switch (measurementType) {
                case "LengthUnit":
                    LengthUnit.valueOf(unit);
                    break;
                case "VolumeUnit":
                    VolumeUnit.valueOf(unit);
                    break;
                case "WeightUnit":
                    WeightUnit.valueOf(unit);
                    break;
                case "TemperatureUnit":
                    TemperatureUnit.valueOf(unit);
                    break;
                default:
                    return false;
            }
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.warning("Invalid unit tested: " + unit + " for measurement type: " + measurementType);
            return false;
        }
    }
}
