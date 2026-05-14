package com.quantitymeasurement.app.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quantitymeasurement.app.model.QuantityInputDTO;
import com.quantitymeasurement.app.model.QuantityMeasurementDTO;
import com.quantitymeasurement.app.service.IQuantityMeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller class governing REST API endpoints for quantity measurements.
 * Handles client requests to measure, convert, and add disparate unit scales
 * while delegating the underlying business logic to the service layer.
 */
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = Logger.getLogger(QuantityMeasurementController.class.getName());

    @Autowired
    private IQuantityMeasurementService service;

    @GetMapping("/status")
    @Operation(summary = "Check deployment status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Jenkins Automated Deployment is working! Hello from AWS Cloud!");
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities")
    public ResponseEntity<QuantityMeasurementDTO> performComparison(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("Received request to perform compare operation");
        return ResponseEntity.ok(service.compare(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert quantity to target unit")
    public ResponseEntity<QuantityMeasurementDTO> performConversion(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.convert(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> performAddition(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.add(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/add-with-target-unit")
    @Operation(summary = "Add two quantities with a target unit")
    public ResponseEntity<QuantityMeasurementDTO> performAdditionWithTargetUnit(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.add(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO(), quantityInputDTO.getTargetQuantityDTO()));
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities")
    public ResponseEntity<QuantityMeasurementDTO> performSubtraction(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.subtract(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/subtract-with-target-unit")
    @Operation(summary = "Subtract two quantities with target unit")
    public ResponseEntity<QuantityMeasurementDTO> performSubtractionWithTargetUnit(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.subtract(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO(), quantityInputDTO.getTargetQuantityDTO()));
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities")
    public ResponseEntity<QuantityMeasurementDTO> performDivision(@Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        return ResponseEntity.ok(service.divide(quantityInputDTO.getThisQuantityDTO(), quantityInputDTO.getThatQuantityDTO()));
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get operation history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(@PathVariable String operation) {
        logger.info("Fetching operation history for: " + operation);
        return ResponseEntity.ok(service.getOperationHistory(operation));
    }

    @GetMapping("/history/type/{type}")
    @Operation(summary = "Get operation history by type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistoryByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getMeasurementsByType(type));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get operation count")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get errored operations history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErroredOperations() {
        return ResponseEntity.ok(service.getErrorHistory());
    }
}
