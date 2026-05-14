package com.quantitymeasurement.app.service;

import java.util.List;

import com.quantitymeasurement.app.model.QuantityDTO;
import com.quantitymeasurement.app.model.QuantityMeasurementDTO;

/**
 * Interface contracting the primary unit measure operations (compare, convert, add, subtract) 
 * available for execution. Establishes the expected input/output bounds across the Service tier.
 */
public interface IQuantityMeasurementService {
    
	public QuantityMeasurementDTO compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    
    public QuantityMeasurementDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);
    
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);
    
    public QuantityMeasurementDTO divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    
    public List<QuantityMeasurementDTO> getOperationHistory(String operation);
    
    public List<QuantityMeasurementDTO> getMeasurementsByType(String type);
    
    public long getOperationCount(String operation);
    
    public List<QuantityMeasurementDTO> getErrorHistory();
}
