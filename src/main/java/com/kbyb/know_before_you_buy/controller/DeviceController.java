package com.kbyb.know_before_you_buy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.service.DevicePrivacyValueService;
import com.kbyb.know_before_you_buy.service.DeviceService;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyService;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final PrivacyPropertyService privacyPropertyService;
    private final DevicePrivacyValueService devicePrivacyValueService;

    public DeviceController(DeviceService deviceService,
            PrivacyPropertyService privacyPropertyService,
            DevicePrivacyValueService devicePrivacyValueService) {
        this.deviceService = deviceService;
        this.privacyPropertyService = privacyPropertyService;
        this.devicePrivacyValueService = devicePrivacyValueService;
    }

    @GetMapping
    public List<Device> getAll() {
        return deviceService.findAll();
    }

    @GetMapping("/id/{id}")
    public Device getById(@PathVariable Integer id) {
        return deviceService.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
    }

    @GetMapping("/{name}")
    public DeviceWithPrivacyValuesDTO getDeviceWithProperties(@PathVariable String name) {
        return deviceService.getDeviceWithPrivacyValues(name);
    }

    @PostMapping
    public Device create(@RequestBody Device device) {
        return deviceService.save(device);
    }

    @PutMapping("/{id}")
    public Device update(@PathVariable Integer id, @RequestBody Device device) {
        device.setId(id);
        return deviceService.save(device);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        deviceService.deleteById(id);
    }

    @PostMapping("/with-privacy-values")
    @Transactional
    public ResponseEntity<?> createDeviceWithValues(@RequestBody DeviceWithPrivacyValuesDTO dto) {
        try {
            Device device = new Device();
            device.setName(dto.getName());
            device.setDeviceType(dto.getDeviceType());
            device.setFunctionalClassification(dto.getFunctionalClassification());
            device.setImageUrl(dto.getImageUrl());
            device.setUrl(dto.getUrl());

            Device savedDevice = deviceService.save(device);

            for (PrivacyValueDTO pv : dto.getPrivacyValues()) {
                PrivacyProperty prop = null;
                if (pv.getPropertyId() != null) {
                    prop = privacyPropertyService.findById(pv.getPropertyId())
                            .orElseThrow(() -> new IllegalArgumentException("The PrivacyProperty with the id = " + pv.getPropertyId() + " was not found"));
                } else if (pv.getPropertyName() != null) {
                    prop = privacyPropertyService.findByName(pv.getPropertyName())
                            .orElseThrow(() -> new IllegalArgumentException("The PrivacyProperty with the name = " + pv.getPropertyName() + " was not found"));
                } else {
                    throw new IllegalArgumentException("privacyValue must include propertyId or propertyName");
                }

                DevicePrivacyValue dpv = new DevicePrivacyValue();
                dpv.setDevice(savedDevice);
                dpv.setPrivacyProperty(prop);

                // Validate and set value
                if (prop.isValidatable()) {
                    String[] allowed = prop.getAllowedValues().split(",");
                    if (pv.getValue().trim().equalsIgnoreCase(allowed[0])) {
                        dpv.setValue(allowed[0]);
                    } else if (pv.getValue().trim().equalsIgnoreCase(allowed[1].trim())) {
                        dpv.setValue(allowed[1]);
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid value for property: " + prop.getName()
                                + ".\nHas to be one of these:\n" + prop.getAllowedValues()
                                + "\nbut was\n" + pv.getValue()
                        );
                    }
                }else{
                    dpv.setValue(pv.getValue());
                }

                devicePrivacyValueService.save(dpv);
            }

            return ResponseEntity.ok(savedDevice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
