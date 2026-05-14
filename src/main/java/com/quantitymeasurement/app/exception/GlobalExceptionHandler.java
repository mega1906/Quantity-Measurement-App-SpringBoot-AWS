package com.quantitymeasurement.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller advice component responsible for intercepting exceptions thrown 
 * across the application and formatting them into standard ErrorResponse objects.
 * Prevents stack traces from leaking to the client interface.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Intercepts malformed or invalid REST payloads failing `@Valid` constraints.
     * Extracts and concatenates localized validation violation messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
        String errorMessage = errorList.stream()
                .map(objErr -> objErr.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.BAD_REQUEST.value();
        error.error = "Quantity Measurement Error";
        error.message = errorMessage;
        error.path = request.getDescription(false).replace("uri=", "");

        logger.warning("Validation failure for payload: " + errorMessage);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Intercepts business logic continuity violations thrown manually by the service tier.
     * Normalizes the response schema specifically to 400 Bad Request statuses emphasizing user-driven errors.
     */
    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<ErrorResponse> handleQuantityMeasurementException(QuantityMeasurementException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.BAD_REQUEST.value();
        error.error = "Quantity Measurement Error";
        error.message = ex.getMessage();
        error.path = request.getDescription(false).replace("uri=", "");

        logger.warning("QuantityMeasurementException thrown: " + ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.NOT_FOUND.value();
        error.error = "Not Found";
        error.message = ex.getMessage();
        error.path = request.getDescription(false).replace("uri=", "");

        logger.warning("Resource not found: " + ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        error.error = "Internal Server Error";
        error.message = ex.getMessage();
        error.path = request.getDescription(false).replace("uri=", "");

        logger.log(Level.SEVERE, "Unhandled global exception occurred", ex);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
