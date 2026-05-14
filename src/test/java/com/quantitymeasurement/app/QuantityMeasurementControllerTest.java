package com.quantitymeasurement.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantitymeasurement.app.controller.QuantityMeasurementController;
import com.quantitymeasurement.app.model.QuantityDTO;
import com.quantitymeasurement.app.model.QuantityInputDTO;
import com.quantitymeasurement.app.model.QuantityMeasurementDTO;
import com.quantitymeasurement.app.service.IQuantityMeasurementService;

@WebMvcTest(QuantityMeasurementController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for unit tests
public class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IQuantityMeasurementService service;

    private QuantityInputDTO createValidInput() {
        QuantityInputDTO input = new QuantityInputDTO();
        
        QuantityDTO q1 = new QuantityDTO();
        q1.setValue(1.0);
        q1.setUnit("FEET");
        q1.setMeasurementType("LengthUnit");
        
        QuantityDTO q2 = new QuantityDTO(); 
        q2.setValue(12.0);
        q2.setUnit("INCHES"); 
        q2.setMeasurementType("LengthUnit");
        
        input.setThisQuantityDTO(q1);
        input.setThatQuantityDTO(q2);
        return input;
    }


    // Object to JSON & HTTP 200 OK Success
    @Test
    public void testCompareQuantities_Returns200_And_ValidJSON() throws Exception {
        QuantityMeasurementDTO mockResult = new QuantityMeasurementDTO();
        mockResult.setResultString("Equal");
        mockResult.setResultValue(1.0);
        mockResult.setResultUnit("FEET");

        Mockito.when(service.compare(Mockito.any(), Mockito.any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidInput())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultString").value("Equal"))
                .andExpect(jsonPath("$.resultValue").value(1.0))
                .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    @Test
    public void testGetOperationHistory_Returns200() throws Exception {
        Mockito.when(service.getOperationHistory("COMPARE")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/quantities/history/operation/COMPARE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }


    // Client Errors (400 Bad Request, 404 Not Found)
    @Test
    public void testValidationFailure_MissingUnit_Returns400() throws Exception {
        QuantityInputDTO badInput = createValidInput();
        badInput.getThisQuantityDTO().setUnit(null); // Intentionally break validation

        mockMvc.perform(post("/api/v1/quantities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Quantity Measurement Error"));
    }

    @Test
    public void testInvalidEndpoint_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/this-endpoint-does-not-exist")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    // Server Errors (500 Internal Server Error)
    @Test
    public void testUnhandledServiceException_Returns500() throws Exception {
        // Force the mock service to throw a generic unhandled Exception
        Mockito.when(service.divide(Mockito.any(), Mockito.any()))
               .thenThrow(new RuntimeException("Simulated catastrophic database failure"));

        mockMvc.perform(post("/api/v1/quantities/divide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidInput())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Simulated catastrophic database failure"));
    }
}
