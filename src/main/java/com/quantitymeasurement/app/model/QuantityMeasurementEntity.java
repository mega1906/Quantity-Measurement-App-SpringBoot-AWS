package com.quantitymeasurement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA Entity mapping to the quantity system's database schema.
 * Represents a historical record of conversion, comparison, or mathematical operations 
 * processed by the service layer, suitable for audit trailing and basic reporting persistence.
 */
@Entity
@Table(name = "quantity_measurement_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "this_value", nullable = false)
    public Double thisValue;

    @Column(name = "this_unit", nullable = false)
    public String thisUnit;

    @Column(name = "this_measurement_type", nullable = false)
    public String thisMeasurementType;

    @Column(name = "that_value", nullable = true)
    public Double thatValue;

    @Column(name = "that_unit", nullable = true)
    public String thatUnit;

    @Column(name = "that_measurement_type", nullable = true)
    public String thatMeasurementType;

    @Column(name = "operation", nullable = false)
    public String operation;

    @Column(name = "result_value")
    public Double resultValue;

    @Column(name = "result_unit")
    public String resultUnit;

    @Column(name = "result_measurement_type")
    public String resultMeasurementType;

    @Column(name = "result_string")
    public String resultString;

    @Column(name = "is_error")
    public boolean isError;

    @Column(name = "error_message")
    public String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public QuantityMeasurementEntity(Double thisValue, String thisUnit, String thisMeasurementType, Double thatValue, String thatUnit, String thatMeasurementType, String operation, Double resultValue, String resultUnit, String resultMeasurementType, String resultString, boolean isError, String errorMessage) {
        this.thisValue = thisValue;
        this.thisUnit = thisUnit;
        this.thisMeasurementType = thisMeasurementType;
        this.thatValue = thatValue;
        this.thatUnit = thatUnit;
        this.thatMeasurementType = thatMeasurementType;
        this.operation = operation;
        this.resultValue = resultValue;
        this.resultUnit = resultUnit;
        this.resultMeasurementType = resultMeasurementType;
        this.resultString = resultString;
        this.isError = isError;
        this.errorMessage = errorMessage;
    }
}
