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

import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;
import com.kbyb.know_before_you_buy.service.DeviceService;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@RestController
@RequestMapping("/device-privacy-values")
public class DevicePrivacyValueController {

    private final DevicePrivacyValueService service;
    private final DeviceService deviceService;
    private final PrivacyPropertyService privacyPropertyService;

    public DevicePrivacyValueController(DevicePrivacyValueService service, DeviceService deviceService, PrivacyPropertyService privacyPropertyService) {
        this.service = service;
        this.deviceService = deviceService;
        this.privacyPropertyService = privacyPropertyService;
    }

    // CREATE ---------------------------------------------------------
    @Operation(summary = "Create a new DevicePrivacyValue")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "DevicePrivacyValue created"),
        @ApiResponse(responseCode = "404", description = "Device or PrivacyProperty with the specified ID was not found")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody DevicePrivacyValue dpv) {
        try {
            // Check for the existence of the related entities before saving the value.
            deviceService.findById(dpv.getDevice().getId())
                    .orElseThrow(() -> new RuntimeException("Device not found"));
            privacyPropertyService.findById(dpv.getPrivacyProperty().getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dpv));
        } catch (RuntimeException e) {
            // Catches RuntimeException if device or privacy property is not found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // READ -----------------------------------------------------------
    @Operation(summary = "Get all DevicePrivacyValues")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all device privacy values")
    })
    @GetMapping
    public ResponseEntity<List<DevicePrivacyValue>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get all DevicePrivacyValues for a Device by DeviceID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved privacy values for the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified ID was not found")
    })
    @GetMapping("/byDeviceId/{deviceId}")
    public ResponseEntity<?> getAllByDeviceId(@PathVariable Integer deviceId) {
        try {
            // Check if device exists first to return 404 if not found.
            deviceService.findById(deviceId)
                    .orElseThrow(() -> new RuntimeException("Device not found"));
            return ResponseEntity.ok(service.findPrivacyValuesByDeviceId(deviceId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all DevicePrivacyValues for a Device by DeviceName")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved privacy values for the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified name was not found")
    })
    @GetMapping("/byDeviceName/{deviceName}")
    public ResponseEntity<?> getAllByDeviceName(@PathVariable String deviceName) {
        try {
            // Check if device exists first to return 404 if not found.
            deviceService.getDeviceWithPrivacyValues(deviceName);
            return ResponseEntity.ok(service.findPrivacyValuesByDeviceName(deviceName));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE ---------------------------------------------------------
    @Operation(summary = "Update DevicePrivacyValue by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the device privacy value"),
        @ApiResponse(responseCode = "404", description = "DevicePrivacyValue with the specified ID was not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DevicePrivacyValue dpv) {
        // Check if the entity exists before attempting to update.
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "DevicePrivacyValue not found for update"));
        }
        dpv.setId(id);
        return ResponseEntity.ok(service.save(dpv));
    }

    @Operation(summary = "Update DevicePrivacyValue by DeviceId and PrivacyPropertyId")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the device privacy value"),
        @ApiResponse(responseCode = "400", description = "Validation error: invalid value for the property"),
        @ApiResponse(responseCode = "404", description = "DevicePrivacyValue with the specified IDs was not found")
    })
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Update DevicePrivacyValue by DeviceName and PrivacyPropertyId")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the device privacy value"),
        @ApiResponse(responseCode = "400", description = "Validation error: invalid value for the property"),
        @ApiResponse(responseCode = "404", description = "DevicePrivacyValue with the specified name or ID was not found")
    })
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE ---------------------------------------------------------
    @Operation(summary = "Delete a DevicePrivacyValue by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted the device privacy value"),
        @ApiResponse(responseCode = "404", description = "DevicePrivacyValue with the specified ID was not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "DevicePrivacyValue not found for deletion"));
        }
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    
    @Operation(summary = "Delete all DevicePrivacyValues by DeviceID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted all device privacy values for the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified ID was not found")
    })
    @DeleteMapping("/byDeviceId/{deviceId}")
    public ResponseEntity<?> deleteByDeviceId(@PathVariable Integer deviceId) {
        if (deviceService.findById(deviceId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not found for deletion"));
        }
        service.deletePrivacyValuesByDeviceId(deviceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all DevicePrivacyValues by DeviceName")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted all device privacy values for the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified name was not found")
    })
    @DeleteMapping("/byDeviceName/{deviceName}")
    public ResponseEntity<?> deleteByDeviceName(@PathVariable String deviceName) {
        try {
            deviceService.getDeviceWithPrivacyValues(deviceName);
            service.deletePrivacyValuesByDeviceName(deviceName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete all DevicePrivacyValues by PrivacyPropertyID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted all device privacy values for the property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified ID was not found")
    })
    @DeleteMapping("/byPrivacyPropertyId/{propertyId}")
    public ResponseEntity<?> deleteByPrivacyPropertyId(@PathVariable Integer propertyId) {
        try {
            privacyPropertyService.findById(propertyId);
            service.deletePrivacyValuesByPrivacyPropertyId(propertyId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete all DevicePrivacyValues by PrivacyPropertyName")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted all device privacy values for the property"),
        @ApiResponse(responseCode = "404", description = "PrivacyProperty with the specified name was not found")
    })
    @DeleteMapping("/byPrivacyPropertyName/{propertyName}")
    public ResponseEntity<?> deleteByPrivacyPropertyName(@PathVariable String propertyName) {
        try {
            privacyPropertyService.findByName(propertyName);
            service.deletePrivacyValuesByPrivacyPropertyName(propertyName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete a DevicePrivacyValue by DeviceId and PrivacyPropertyId")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted the device privacy value"),
        @ApiResponse(responseCode = "404", description = "DevicePrivacyValue with the specified IDs was not found")
    })
    @DeleteMapping("/byDeviceId/{deviceId}/byPropertyId/{propertyId}")
    public ResponseEntity<?> deleteByDeviceIdAndPropertyId(
            @PathVariable Integer deviceId,
            @PathVariable Integer propertyId) {
        try {
            service.getDpvByDeviceIdAndPropertyId(deviceId, propertyId);
            service.deleteByPrivacyPropertyIdAndDeviceId(deviceId, propertyId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}