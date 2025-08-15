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

import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;
import com.kbyb.know_before_you_buy.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DevicePrivacyValueService devicePrivacyValueService;

    // Create ----------------------------------------------------------

    //TESTED
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
            // Example: device name already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    

    //TESTED
    @Operation(summary = "Create a new Device including its Privacy Values")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Device with Privacy Properties created"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/with-privacy-values")
    public ResponseEntity<?> createDeviceWithPrivacyPropertyValues(@RequestBody DeviceWithPrivacyValuesDTO dto) {
        try {
            Device savedDevice = deviceService.createDeviceWithValues(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // Read ------------------------------------------------------------

    @Operation(summary = "Get all devices")
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @Operation(summary = "Get all devices including their privacy values")
    @GetMapping("/with-privacy-values")
    public ResponseEntity<List<DeviceWithPrivacyValuesDTO>> getAllDevicesWithPrivacyProperties() {
        return ResponseEntity.ok(deviceService.getAllDevicesWithPrivacyProperties());
    }

    @Operation(summary = "Get all device names")
    @GetMapping("/findAllNames")
    public ResponseEntity<List<String>> findAllNames() {
        return ResponseEntity.ok(deviceService.findAllNames());
    }

    @Operation(summary = "Get a device by its ID")
    @GetMapping("/id/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Integer id) {
        return ResponseEntity.ok(
            deviceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"))
        );
    }

    @Operation(summary = "Get a device including privacy values by its name")
    @GetMapping("/byName/{name}")
    public ResponseEntity<DeviceWithPrivacyValuesDTO> getDeviceWithPrivacyPropertiesByDeviceName(@PathVariable String name) {
        return ResponseEntity.ok(deviceService.getDeviceWithPrivacyValues(name));
    }

    // Update -----------------------------------------------------------

    @Operation(summary = "Update a device")
    @PutMapping("/{id}")
    public ResponseEntity<Device> update(@PathVariable Integer id, @RequestBody Device device) {
        device.setId(id);
        return ResponseEntity.ok(deviceService.save(device));
    }

    // Delete -----------------------------------------------------------

    @Operation(summary = "Delete a device (and all associated privacy values)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        devicePrivacyValueService.deletePrivacyValuesByDeviceId(id);
        deviceService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
