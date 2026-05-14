package com.quantitymeasurement.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Home", description = "Root mapping controller")
public class HomeController {

    @Operation(summary = "Redirects to Swagger UI Documentation")
    @GetMapping({"/", "/api/v1/quantities"})
    public RedirectView redirectToSwagger() {
        // Redirect the root URL "/" to the Swagger UI interactive documentation.
        return new RedirectView("/swagger-ui.html");
    }
}