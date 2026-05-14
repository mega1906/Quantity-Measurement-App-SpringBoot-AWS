package com.quantitymeasurement.app.exception;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standard error response payload representing the structured details 
 * returned to the client whenever an API exception or error occurs.
 */
@Data
public class ErrorResponse {
    public LocalDateTime timestamp;
    public int status;
    public String error;
    public String message;
    public String path;
}
