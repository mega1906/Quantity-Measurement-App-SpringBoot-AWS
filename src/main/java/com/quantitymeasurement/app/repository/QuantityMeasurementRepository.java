package com.quantitymeasurement.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quantitymeasurement.app.model.QuantityMeasurementEntity;

/**
 * Spring Data JPA Repository interface handling persistence and retrieval of 
 * QuantityMeasurementEntity records from the relational database via declarative queries.
 */
@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {


    List<QuantityMeasurementEntity> findByOperation(String operation);


    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);


    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);


    @Query("SELECT q FROM QuantityMeasurementEntity q WHERE q.operation = :operation AND q.isError = false")
    List<QuantityMeasurementEntity> countByOperationAndIsErrorFalse(@Param("operation") String operation);


    long countByOperation(String operation);


    List<QuantityMeasurementEntity> findByIsErrorTrue();
}
