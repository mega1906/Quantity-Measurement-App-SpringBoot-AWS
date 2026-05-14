package com.quantitymeasurement.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.quantitymeasurement.app.model.QuantityDTO;
import com.quantitymeasurement.app.model.QuantityInputDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/quantities";
    }

    private QuantityDTO createDTO(double val, String unit, String type) {
        QuantityDTO dto = new QuantityDTO();
        dto.setValue(val);
        dto.setUnit(unit);
        dto.setMeasurementType(type);
        return dto;
    }

    // 1. Edge Case: Floating Point Precision check
    @Test
    public void testCompare_GallonToLiters_PrecisionCheck_ReturnsEqual() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(1.0, "GALLON", "VolumeUnit"));
        input.setThatQuantityDTO(createDTO(3.785, "LITRE", "VolumeUnit"));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/compare", input, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"resultString\":\"Equal\"");
    }

    // 2. Edge Case: Temperature Formula Offset Verification
    @Test
    public void testConvert_FahrenheitToCelsius_ValidatesFormula() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(212.0, "FAHRENHEIT", "TemperatureUnit"));
        input.setThatQuantityDTO(createDTO(0.0, "CELSIUS", "TemperatureUnit")); // target unit

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/convert", input, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 212F should precisely convert to 100C
        assertThat(response.getBody()).contains("\"resultValue\":100.0");
        assertThat(response.getBody()).contains("\"resultUnit\":\"CELSIUS\"");
    }

    // 3. Error Combination: Mixing Incompatible Types
    @Test
    public void testAdd_LengthToWeight_ThrowsBadRequestError() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(10.0, "FEET", "LengthUnit"));
        input.setThatQuantityDTO(createDTO(5.0, "KILOGRAM", "WeightUnit"));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", input, String.class);
        
        // Ensure GlobalExceptionHandler catches it and returns 400
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Cannot perform operation between different measurement categories");
    }

    // 4. Complete Combination: Adding with a separate Target Unit
    @Test
    public void testAdd_GramsAndKilograms_WithTargetUnitTonne() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(1000.0, "GRAM", "WeightUnit")); // 1 KG
        input.setThatQuantityDTO(createDTO(999.0, "KILOGRAM", "WeightUnit")); // 999 KG
        input.setTargetQuantityDTO(createDTO(0.0, "TONNE", "WeightUnit")); // Total: 1000 KG = 1 Tonne

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add-with-target-unit", input, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"resultValue\":1.0");
        assertThat(response.getBody()).contains("\"resultUnit\":\"TONNE\"");
    }

    // 5. Fatal Error: Divide by zero protection
    @Test
    public void testDivide_ByZero_ReturnsHandledError() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(10.0, "FEET", "LengthUnit"));
        input.setThatQuantityDTO(createDTO(0.0, "INCHES", "LengthUnit"));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/divide", input, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Divide by zero is not allowed");
    }

    /**
     * This single test runs 5 times automatically, testing different 
     * unit combinations, operations, and expected mathematical outcomes.
     */
    @ParameterizedTest
    @CsvSource({
        // val1, unit1, type1, val2, unit2, type2, operation, expectedResultVal, expectedResultUnit
        "1.0, FEET, LengthUnit, 12.0, INCHES, LengthUnit, add, 24.0, INCHES",
        "1.0, GALLON, VolumeUnit, 3.785, LITRE, VolumeUnit, subtract, 0.0, LITRE",
        "1000.0, GRAM, WeightUnit, 1.0, KILOGRAM, WeightUnit, compare, 0.0, KILOGRAM", // Compare ignores output unit, checks for "Equal"
        "50.0, CELSIUS, TemperatureUnit, 2.0, CELSIUS, TemperatureUnit, divide, 25.0, CELSIUS",
        "36.0, INCHES, LengthUnit, 0.0, YARDS, LengthUnit, convert, 1.0, YARDS" // Converting 36 inches to target unit YARDS
    })
    public void testComprehensiveCombinations(
            double v1, String u1, String t1, 
            double v2, String u2, String t2, 
            String op, double expectedVal, String expectedUnit) {

        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(v1, u1, t1));
        
        // If it's a convert operation, we use 'thatQuantity' as the target unit carrier
        if (op.equals("convert")) {
            input.setThatQuantityDTO(createDTO(0.0, expectedUnit, t1)); 
        } else {
            input.setThatQuantityDTO(createDTO(v2, u2, t2));
            input.setTargetQuantityDTO(createDTO(0.0, expectedUnit, t1)); // Enforce target unit for math
        }

        // Determine correct endpoint
        String endpoint = op.equals("add") || op.equals("subtract") ? op + "-with-target-unit" : op;
        
        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/" + endpoint, input, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        if (op.equals("compare")) {
            assertThat(response.getBody()).contains("\"resultString\":\"Equal\"");
        } else {
            assertThat(response.getBody()).contains("\"resultValue\":" + expectedVal);
            assertThat(response.getBody()).contains("\"resultUnit\":\"" + expectedUnit + "\"");
        }
    }
    
    @Test
    public void NullMeasurementType_Returns400() {
        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(createDTO(1.0, "FEET", null)); // Intentionally missing type
        input.setThatQuantityDTO(createDTO(1.0, "FEET", "LengthUnit"));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", input, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void ExtremeOverflow_Returns400() {
        QuantityInputDTO input = new QuantityInputDTO();
        // Trying to double the absolute maximum number Java can hold
        input.setThisQuantityDTO(createDTO(Double.MAX_VALUE, "FEET", "LengthUnit"));
        input.setThatQuantityDTO(createDTO(Double.MAX_VALUE, "FEET", "LengthUnit"));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", input, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("exceeded physical calculation bounds");
    }
}
