# Quantity Measurement App

A Spring Boot REST API for comparing, converting, adding, subtracting, and dividing physical quantities across measurement systems.

## Project Overview

This service accepts quantity requests in JSON, validates the incoming units and measurement types, performs conversions or arithmetic, and returns structured results. Every operation is saved to persistent storage and can be retrieved later through history endpoints.

## Features

- Supports quantity operations for:
  - `LengthUnit`: `FEET`, `INCHES`, `YARDS`, `CENTIMETERS`
  - `VolumeUnit`: `LITRE`, `MILLILITER`, `GALLON`
  - `WeightUnit`: `MILLIGRAM`, `GRAM`, `KILOGRAM`, `POUND`, `TONNE`
  - `TemperatureUnit`: `CELSIUS`, `FAHRENHEIT`
- Validates `value`, `unit`, and `measurementType` in incoming requests
- Converts between compatible units automatically
- Adds and subtracts quantities with optional target unit output
- Compares quantities and calculates division results
- Stores operation history and exposes audit endpoints
- Exposes Swagger UI for API exploration
- Uses Spring Security with stateless, permit-all configuration for REST access

## Base URL

`http://localhost:8080/api/v1/quantities`

## API Endpoints

### Health / Status

- `GET /status`
  - Returns a simple deployment health message.

### Quantity Operations

- `POST /compare`
- `POST /convert`
- `POST /add`
- `POST /add-with-target-unit`
- `POST /subtract`
- `POST /subtract-with-target-unit`
- `POST /divide`

### History and Metrics

- `GET /history/operation/{operation}`
- `GET /history/type/{type}`
- `GET /count/{operation}`
- `GET /history/errored`

## Run Locally

1. Install Java 21 and Maven.
2. Open the project in Spring Tool Suite (STS).
3. In STS, locate `QuantityMeasurementApplication.java` under `src/main/java`.
4. Right-click `QuantityMeasurementApplication.java` and select **Run As > Spring Boot App**.
5. When the application starts, open Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

6. Access the OpenAPI docs:

```text
http://localhost:8080/api-docs
```

## Configuration

The project currently uses MySQL configuration values defined in `src/main/resources/application.properties`. Update these values for local development or AWS deployment.

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`

Because the project also includes H2 on the classpath, it can be adapted for in-memory testing if desired.

## Running Tests

Execute:

```bash
./mvnw test
```

This runs controller and service test suites included under `src/test/java`.