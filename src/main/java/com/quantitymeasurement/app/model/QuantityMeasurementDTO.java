package com.quantitymeasurement.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Output Data Transfer Object summarizing the result of a calculated operation.
 * Formats database entities or service responses into an easily serializable 
 * structure appropriate for REST API consumption.
 */
@Data
public class QuantityMeasurementDTO {
    public Double thisValue;
    public String thisUnit;
    public String thisMeasurementType;
    public Double thatValue;
    public String thatUnit;
    public String thatMeasurementType;
    public String operation;
    public String resultString;
    public Double resultValue;
    public String resultUnit;
    public String resultMeasurementType;


    @JsonProperty("error")
    public boolean isError;
    public String errorMessage;

    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity entity) {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(entity.getThisValue());
        dto.setThisUnit(entity.getThisUnit());
        dto.setThisMeasurementType(entity.getThisMeasurementType());
        dto.setThatValue(entity.getThatValue());
        dto.setThatUnit(entity.getThatUnit());
        dto.setThatMeasurementType(entity.getThatMeasurementType());
        dto.setOperation(entity.getOperation());
        dto.setResultString(entity.getResultString());
        dto.setResultValue(entity.getResultValue());
        dto.setResultUnit(entity.getResultUnit());
        dto.setResultMeasurementType(entity.getResultMeasurementType());
        dto.setError(entity.isError());
        dto.setErrorMessage(entity.getErrorMessage());
        return dto;
    }

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream()
            .map(QuantityMeasurementDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
