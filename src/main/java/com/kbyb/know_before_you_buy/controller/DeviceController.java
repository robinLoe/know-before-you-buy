package com.kbyb.know_before_you_buy.controller;

import java.util.ArrayList;
import java.util.List;

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

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DevicePrivacyValueService devicePrivacyValueService;

    // Create
    @PostMapping
    public Device createDevice(@RequestBody Device device) {
        return deviceService.save(device);
    }

    @PostMapping("/with-privacy-values")
    public ResponseEntity<?> createDeviceWithPrivacyPropertyValues(@RequestBody DeviceWithPrivacyValuesDTO dto) {
        try {
            Device savedDevice = deviceService.createDeviceWithValues(dto);
            return ResponseEntity.ok(savedDevice);
        } catch (IllegalArgumentException e) {
            // Business validation error (e.g. invalid property ID or value)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Unexpected runtime error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // Read

    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.findAll();
    }

    @GetMapping("/with-privacy-values")
    public List<DeviceWithPrivacyValuesDTO> getAllDevicesWithPrivacyProperties() {
        return deviceService.getAllDevicesWithPrivacyProperties();
    }

    @GetMapping("/findAllNames")
    public ArrayList<String> findAllNames() {
        return deviceService.findAllNames();
    }
    
    

    @GetMapping("/id/{id}")
    public Device getDeviceById(@PathVariable Integer id) {
        return deviceService.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
    }

    @GetMapping("/byName/{name}")
    public DeviceWithPrivacyValuesDTO getDeviceWithPrivacyPropertiesByDeviceName(@PathVariable String name) {
        return deviceService.getDeviceWithPrivacyValues(name);
    }


    // Update

    @PutMapping("/{id}")
    public Device update(@PathVariable Integer id, @RequestBody Device device) {
        device.setId(id);
        return deviceService.save(device);
    }


    // Delete

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        devicePrivacyValueService.deletePrivacyValuesForDevice(id);
        deviceService.deleteById(id);
    }

}
