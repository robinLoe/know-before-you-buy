package com.kbyb.know_before_you_buy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST controller for managing PrivacyProperty entities.
 * Provides endpoints for CRUD operations related to privacy properties.
 */
@RestController
@RequestMapping("/privacy-properties")
public class PrivacyPropertyController {

    private final PrivacyPropertyService service;
    private final DevicePrivacyValueService devicePrivacyValueService;

    public PrivacyPropertyController(PrivacyPropertyService service, DevicePrivacyValueService devicePrivacyValueService) {
        this.service = service;
        this.devicePrivacyValueService = devicePrivacyValueService;
    }

    // CREATE ---------------------------------------------------------
    @Operation(summary = "Create a new PrivacyProperty")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "PrivacyProperty created"),
        @ApiResponse(responseCode = "409", description = "PrivacyProperty with that name already exists")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PrivacyProperty pp) {
        try{
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(pp));
        } catch (RuntimeException e) {
            // Catches an error if a PrivacyProperty with the same name already exists.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // READ -----------------------------------------------------------
    @Operation(summary = "Get all PrivacyProperties")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all privacy properties")
    })
    @GetMapping
    public ResponseEntity<List<PrivacyProperty>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get PrivacyProperty by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the privacy property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified ID was not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try{
            return ResponseEntity.ok(service.findById(id));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get PrivacyProperty by Name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the privacy property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified name was not found")
    })
    @GetMapping("/byName/{name}")
    public ResponseEntity<?> getByName(@PathVariable String name) {
        try{
            return ResponseEntity.ok(service.findByName(name));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE ---------------------------------------------------------
    @Operation(summary = "Update PrivacyProperty by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the privacy property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified ID was not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Integer id, @RequestBody PrivacyProperty pp) {
        try{
            service.findById(id);
            pp.setId(id);
            return ResponseEntity.ok(service.save(pp));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE ---------------------------------------------------------
    @Operation(summary = "Delete PrivacyProperty by ID including linked DevicePrivacyValues")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted the privacy property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified ID was not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        try{
        devicePrivacyValueService.deletePrivacyValuesByPrivacyPropertyId(id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete PrivacyProperty by Name including linked DevicePrivacyValues")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted the privacy property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified name was not found")
    })
    @DeleteMapping("/byName/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        try{
            devicePrivacyValueService.deletePrivacyValuesByPrivacyPropertyName(name);
            service.deleteByName(name);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}