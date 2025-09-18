package com.kbyb.know_before_you_buy.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kbyb.know_before_you_buy.dto.CsvImportResult;
import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.dto.FailedImportEntry;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;
import com.kbyb.know_before_you_buy.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing Device entities. Provides endpoints for CRUD
 * operations and CSV import.
 */
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DevicePrivacyValueService devicePrivacyValueService;

    // Create ----------------------------------------------------------
    @Operation(summary = "Create a new Device (without privacy values)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Device created"),
        @ApiResponse(responseCode = "409", description = "Device with that name already exists")
    })
    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody Device device) {
        try {
            Device saved = deviceService.save(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            // Catches an error if a device with the same name already exists.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Create a new Device including its Privacy Values")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Device with Privacy Properties created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Device with that name already exists")
    })
    @PostMapping("/with-privacy-values")
    public ResponseEntity<?> createDeviceWithPrivacyPropertyValues(@RequestBody DeviceWithPrivacyValuesDTO dto) {
        try {
            Device savedDevice = deviceService.createDeviceWithValues(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
        } catch (IllegalArgumentException e) {
            // Handles validation errors, e.g., invalid privacy value format.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Handles other runtime errors, e.g., a device with the same name already exists.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Create one ore more Devices including its Privacy Values via CSV Upload")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV imported, result body shows successful and failed imports."),
        @ApiResponse(responseCode = "400", description = "File is empty or invalid CSV format."),
        @ApiResponse(responseCode = "500", description = "Internal server error while processing the CSV file.")
    })
    @PostMapping("/csv")
    public ResponseEntity<CsvImportResult> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            CsvImportResult result = new CsvImportResult();
            result.addFailure(new FailedImportEntry("No File", "Please upload a file to analyze."));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        try {
            String csvContent = new String(file.getBytes());
            CsvImportResult result = deviceService.createDevicesFromCSV(csvContent);

            // if succesful return OK and result
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (IOException e) {
            // error handling
            CsvImportResult result = new CsvImportResult();
            result.addFailure(new FailedImportEntry("Failed reading file", "Error processing the CSV-file: " + e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Read ------------------------------------------------------------

    @Operation(summary = "Get all devices")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of devices")
    })
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @Operation(summary = "Get all devices including their privacy values")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of devices with privacy values")
    })
    @GetMapping("/with-privacy-values")
    public ResponseEntity<List<DeviceWithPrivacyValuesDTO>> getAllDevicesWithPrivacyProperties() {
        return ResponseEntity.ok(deviceService.getAllDevicesWithPrivacyProperties());
    }

    @Operation(summary = "Get all device names")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all device names")
    })
    @GetMapping("/findAllNames")
    public ResponseEntity<List<String>> findAllNames() {
        return ResponseEntity.ok(deviceService.findAllNames());
    }

    @Operation(summary = "Get a device by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified ID was not found")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(
                    deviceService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Device not found"))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get a device including privacy values by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the device and its privacy values"),
        @ApiResponse(responseCode = "404", description = "Device with the specified name was not found")
    })
    @GetMapping("/byName/{name}")
    public ResponseEntity<?> getDeviceWithPrivacyPropertiesByDeviceName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(deviceService.getDeviceWithPrivacyValues(name));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // Update -----------------------------------------------------------
    @Operation(summary = "Update a device")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified ID was not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Device device) {
        if (deviceService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not found for update"));
        }
        device.setId(id);
        return ResponseEntity.ok(deviceService.save(device));
    }

// Delete -----------------------------------------------------------
    @Operation(summary = "Delete a device (and all associated privacy values)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully deleted the device"),
        @ApiResponse(responseCode = "404", description = "Device with the specified ID was not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (deviceService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not found for deletion"));
        }
        devicePrivacyValueService.deletePrivacyValuesByDeviceId(id);
        deviceService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
