package com.quantitymeasurement.app.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quantitymeasurement.app.exception.QuantityMeasurementException;
import com.quantitymeasurement.app.model.OperationType;
import com.quantitymeasurement.app.model.QuantityDTO;
import com.quantitymeasurement.app.model.QuantityMeasurementDTO;
import com.quantitymeasurement.app.model.QuantityMeasurementEntity;
import com.quantitymeasurement.app.repository.QuantityMeasurementRepository;
import com.quantitymeasurement.app.service.IQuantityMeasurementService;

/**
 * Service implementation layer handling core domain logic for unit conversions,
 * comparisons,
 * and mathematical operations (addition, subtraction). Serves as the
 * intermediary between
 * API controllers and database repositories.
 */
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = Logger.getLogger(QuantityMeasurementServiceImpl.class.getName());

    @Autowired
    private QuantityMeasurementRepository repository;

    /**
     * Converts an inbound value of a specific unit to its established base metric.
     * For instance, lengths are baselined to INCHES, volumes to LITRES, and weights
     * to KILOGRAMS.
     * 
     * @param value The raw numeric quantity.
     * @param unit  The source unit string (e.g., "FEET", "GALLON").
     * @return The standardized base value as a double.
     * @throws QuantityMeasurementException if the unit is unsupported or null.
     */
    private double getBaseValue(double value, String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new QuantityMeasurementException("Unit cannot be null or empty.");
        }

        return switch (unit.toUpperCase()) {
            case "FEET" -> value * 12.0;
            case "YARDS" -> value * 36.0;
            case "CENTIMETERS" -> value / 2.54;
            case "INCHES" -> value;
            case "GALLON" -> value * 3.785;
            case "LITRE" -> value;
            case "MILLILITER" -> value / 1000.0;
            case "KILOGRAM" -> value;
            case "GRAM" -> value / 1000.0;
            case "TONNE" -> value * 1000.0;
            case "CELSIUS" -> value;
            case "FAHRENHEIT" -> (value - 32.0) * 5.0 / 9.0;
            default -> throw new QuantityMeasurementException("Unsupported unit for calculation: " + unit);
        };
    }

    /**
     * Reverts a baselined metric back into a requested target unit scale.
     * Utilized as the conclusive step during conversions or cross-unit mathematical
     * operations.
     * 
     * @param baseValue  The standardized numeric value.
     * @param targetUnit The desired output unit format.
     * @return The mathematically converted value formatting to the target unit.
     * @throws QuantityMeasurementException if bounds are exceeded or unit is
     *                                      malformed.
     */
    private double convertFromBaseValue(double baseValue, String targetUnit) {
        if (targetUnit == null || targetUnit.trim().isEmpty()) {
            throw new QuantityMeasurementException("Target unit cannot be null or empty.");
        }

        if (Double.isInfinite(baseValue) || Double.isNaN(baseValue)) {
            throw new QuantityMeasurementException("Mathematical result exceeded physical calculation bounds.");
        }

        return switch (targetUnit.toUpperCase()) {
            case "FEET" -> baseValue / 12.0;
            case "YARDS" -> baseValue / 36.0;
            case "CENTIMETERS" -> baseValue * 2.54;
            case "INCHES" -> baseValue;
            case "GALLON" -> baseValue / 3.785;
            case "LITRE" -> baseValue;
            case "MILLILITER" -> baseValue * 1000.0;
            case "KILOGRAM" -> baseValue;
            case "GRAM" -> baseValue * 1000.0;
            case "TONNE" -> baseValue / 1000.0;
            case "CELSIUS" -> baseValue;
            case "FAHRENHEIT" -> (baseValue * 9.0 / 5.0) + 32.0;
            default -> throw new QuantityMeasurementException("Unsupported target unit: " + targetUnit);
        };
    }

    /**
     * Internal validator ensuring logical continuity before evaluating quantities.
     * Prevents operations attempting to mix incompatible dimensions (e.g.,
     * Temperature and Weight).
     */
    private void validateSameMeasurementType(QuantityDTO q1, QuantityDTO q2) {
        if (q1.getMeasurementType() == null || q2.getMeasurementType() == null) {
            throw new QuantityMeasurementException("Measurement type must be specified for both quantities.");
        }
        if (!q1.getMeasurementType().equals(q2.getMeasurementType())) {
            throw new QuantityMeasurementException("Cannot perform operation between different measurement categories: "
                    + q1.getMeasurementType() + " and " + q2.getMeasurementType());
        }
    }

    /**
     * Central processing engine routing all validated numerical operations.
     * Computes the mathematical resolution across standard baselines and persists
     * an audit
     * trail entity recording the operation flow, handling error propagation
     * seamlessly.
     * 
     * @param q1     Primary execution quantity.
     * @param q2     Secondary interacting quantity (can be null for single-entity
     *               checks).
     * @param target Desired output quantity formatting constraints.
     * @param opType The algebraic or logical mechanism to invoke (ADD, COMPARE,
     *               etc.).
     * @return Formatted QuantityMeasurementDTO detailing successful outputs or
     *         wrapped error states.
     */
    private QuantityMeasurementDTO processAndSave(QuantityDTO q1, QuantityDTO q2, QuantityDTO target,
            OperationType opType) {
        double resultVal = 0.0;
        String resultStr = "";
        boolean isErr = false;
        String errMsg = null;

        logger.info("Starting processing for operation: " + opType + " with primary entity value: " + q1.getValue());

        try {
            if (q2 != null)
                validateSameMeasurementType(q1, q2);

            double base1 = getBaseValue(q1.getValue(), q1.getUnit());
            double base2 = (q2 != null) ? getBaseValue(q2.getValue(), q2.getUnit()) : 0.0;

            switch (opType) {
                case COMPARE:
                    resultStr = (Math.abs(base1 - base2) <= 0.001) ? "Equal" : "Not Equal";
                    break;
                case ADD:
                    resultVal = base1 + base2;
                    break;
                case SUBTRACT:
                    resultVal = base1 - base2;
                    break;
                case DIVIDE:
                    if (base2 == 0)
                        throw new QuantityMeasurementException("Divide by zero is not allowed.");
                    resultVal = base1 / base2;
                    break;
                case CONVERT:
                    resultVal = base1;
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Exception encountered during base unit conversion or mathematical operation: " + opType, e);
            isErr = true;
            errMsg = e.getMessage();
        }

        String targetUnitStr;
        if (target != null) {
            targetUnitStr = target.getUnit();
        } else if (opType == OperationType.CONVERT && q2 != null) {
            targetUnitStr = q2.getUnit();
        } else {
            targetUnitStr = q1.getUnit();
        }

        try {
            if (!isErr && (opType == OperationType.ADD || opType == OperationType.SUBTRACT
                    || opType == OperationType.DIVIDE || opType == OperationType.CONVERT)) {
                resultVal = convertFromBaseValue(resultVal, targetUnitStr);
            }
        } catch (Exception e) {
            isErr = true;
            errMsg = e.getMessage();
        }

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                (q2 != null) ? q2.getValue() : null, (q2 != null) ? q2.getUnit() : null,
                (q2 != null) ? q2.getMeasurementType() : null,
                opType.name(), resultVal, targetUnitStr, q1.getMeasurementType(), resultStr, isErr, errMsg);

        repository.save(entity);

        if (isErr) {
            logger.warning("Operation failed and was recorded as an error state: " + errMsg);
            throw new QuantityMeasurementException(errMsg);
        }

        logger.info("Successfully processed and saved operation: " + opType + " with result: " + resultVal + " "
                + targetUnitStr);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, null, OperationType.COMPARE);
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, null, OperationType.CONVERT);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, null, OperationType.ADD);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, targetUnitDTO, OperationType.ADD);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, null, OperationType.SUBTRACT);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, targetUnitDTO, OperationType.SUBTRACT);
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return processAndSave(thisQuantityDTO, thatQuantityDTO, null, OperationType.DIVIDE);
    }

    @Override
    public List<QuantityMeasurementDTO> getOperationHistory(String operation) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByOperation(operation));
    }

    @Override
    public List<QuantityMeasurementDTO> getMeasurementsByType(String type) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByThisMeasurementType(type));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperation(operation);
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }
}
