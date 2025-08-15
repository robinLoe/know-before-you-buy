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

import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;

import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping("/device-privacy-values")
public class DevicePrivacyValueController {

    private final DevicePrivacyValueService service;

    public DevicePrivacyValueController(DevicePrivacyValueService service) {
        this.service = service;
    }

    // CREATE ---------------------------------------------------------
    @Operation(summary = "Create a new DevicePrivacyValue")
    @PostMapping
    public ResponseEntity<DevicePrivacyValue> create(@RequestBody DevicePrivacyValue dpv) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dpv));
    }

    // READ -----------------------------------------------------------
    @Operation(summary = "Get all DevicePrivacyValues")
    @GetMapping
    public ResponseEntity<List<DevicePrivacyValue>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get all DevicePrivacyValues for a Device by DeviceID")
    @GetMapping("/byDeviceId/{deviceId}")
    public ResponseEntity<List<PrivacyValueDTO>> getAllByDeviceId(@PathVariable Integer deviceId) {
        return ResponseEntity.ok(service.findPrivacyValuesByDeviceId(deviceId));
    }

    @Operation(summary = "Get all DevicePrivacyValues for a Device by DeviceName")
    @GetMapping("/byDeviceName/{deviceName}")
    public ResponseEntity<List<PrivacyValueDTO>> getAllByDeviceName(@PathVariable String deviceName) {
        return ResponseEntity.ok(service.findPrivacyValuesByDeviceName(deviceName));
    }

    // UPDATE ---------------------------------------------------------
    @Operation(summary = "Update DevicePrivacyValue by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DevicePrivacyValue> update(@PathVariable Integer id, @RequestBody DevicePrivacyValue dpv) {
        dpv.setId(id);
        return ResponseEntity.ok(service.save(dpv));
    }

    // TO TEST
    @Operation(summary = "Update DevicePrivacyValue by DeviceId and PrivacyPropertyId")
    @PutMapping("/byDeviceId/{deviceId}/byPropertyId/{propertyId}")
    public ResponseEntity<?> updateByDeviceIdAndPropertyId(
            @PathVariable Integer deviceId,
            @PathVariable Integer propertyId,
            @RequestBody String value) {
            
        try{
            DevicePrivacyValue dpv = service.getDpvByDeviceIdAndPropertyId(deviceId, propertyId);
            return ResponseEntity.ok(service.save(service.validateAndUpdateDpvValue(value, dpv)));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    //TO TEST
    @Operation(summary = "Update DevicePrivacyValue by DeviceName and PrivacyPropertyId")
    @PutMapping("/byDeviceName/{deviceName}/byPropertyId/{propertyId}")
    public ResponseEntity<?> updateByDeviceNameAndPropertyId(
            @PathVariable String deviceName,
            @PathVariable Integer propertyId,
            @RequestBody String value) {
        
        try{
            DevicePrivacyValue dpv = service.getDpvByDeviceNameAndPropertyId(deviceName, propertyId);
            return ResponseEntity.ok(service.save(service.validateAndUpdateDpvValue(value, dpv)));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }

    }

    // DELETE ---------------------------------------------------------
    @Operation(summary = "Delete a DevicePrivacyValue by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /*
    @Operation(summary = "Delete all DevicePrivacyValues for a Device by ID")
    @DeleteMapping("/byDeviceId/{deviceId}")
    public ResponseEntity<Void> deleteByDeviceId(@PathVariable Integer deviceId) {
        service.deleteByDeviceId(deviceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all DevicePrivacyValues for a Device by Name")
    @DeleteMapping("/byDeviceName/{deviceName}")
    public ResponseEntity<Void> deleteByDeviceName(@PathVariable String deviceName) {
        service.deleteByDeviceName(deviceName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all DevicePrivacyValues for a PrivacyProperty by ID")
    @DeleteMapping("/byPrivacyPropertyId/{propertyId}")
    public ResponseEntity<Void> deleteByPrivacyPropertyId(@PathVariable Integer propertyId) {
        service.deleteByPrivacyPropertyId(propertyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a DevicePrivacyValue by DeviceId and PrivacyPropertyId")
    @DeleteMapping("/byDeviceId/{deviceId}/byPropertyId/{propertyId}")
    public ResponseEntity<Void> deleteByDeviceIdAndPropertyId(
            @PathVariable Integer deviceId,
            @PathVariable Integer propertyId) {
        service.deleteByDeviceIdAndPropertyId(deviceId, propertyId);
        return ResponseEntity.ok().build();
    } */
}