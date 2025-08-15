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
    @PostMapping
    public ResponseEntity<PrivacyProperty> create(@RequestBody PrivacyProperty pp) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(pp));
    }

    // READ -----------------------------------------------------------
    @Operation(summary = "Get all PrivacyProperties")
    @GetMapping
    public ResponseEntity<List<PrivacyProperty>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get PrivacyProperty by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try{
            return ResponseEntity.ok(service.findById(id));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get PrivacyProperty by Name")
    @GetMapping("/byName/{name}")
    public ResponseEntity<?> getByName(@PathVariable String name) {
        try{
            return ResponseEntity.ok(service.findByName(name));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE ---------------------------------------------------------
    @Operation(summary = "Update PrivacyProperty by ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Integer id, @RequestBody PrivacyProperty pp) {
        try{
            service.findById(id);
            pp.setId(id);
            return ResponseEntity.ok(service.save(pp));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Update PrivacyProperty by Name")
    @PutMapping("/byName/{name}")
    public ResponseEntity<?> updateByName(@PathVariable String name, @RequestBody PrivacyProperty pp) {
        try{
            Integer oldId = service.findByName(name).getId();
            pp.setId(oldId);
            return ResponseEntity.ok(service.save(pp));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE ---------------------------------------------------------
    @Operation(summary = "Delete PrivacyProperty by ID including linked DevicePrivacyValues")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        
        devicePrivacyValueService.deletePrivacyValuesByPrivacyPropertyId(id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete PrivacyProperty by Name including linked DevicePrivacyValues")
    @DeleteMapping("/byName/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        try{
            devicePrivacyValueService.deletePrivacyValuesByPrivacyPropertyName(name);
            service.deleteByName(name);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}