package com.quantitymeasurement.app.exception;

/**
 * Custom runtime exception utilized to flag invalid business logic operations
 * such as unsupported unit conversions, null unit types, or incompatible operations.
 */
public class QuantityMeasurementException extends RuntimeException {
    public QuantityMeasurementException(String message) {
        super(message);
    }
}
