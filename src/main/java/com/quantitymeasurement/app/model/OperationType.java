package com.quantitymeasurement.app.model;

/**
 * Enumeration of supported operations on quantities.
 * Determines the calculation path or logic flow executed by the processing services.
 */
public enum OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    COMPARE,
    CONVERT;

    public String getDisplayName() {
        return this.name().toLowerCase();
    }
}
