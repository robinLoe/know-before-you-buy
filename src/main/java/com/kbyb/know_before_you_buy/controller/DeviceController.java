package com.kbyb.know_before_you_buy.controller;

import java.util.List;

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
    public Device createDeviceWithValues(@RequestBody DeviceWithPrivacyValuesDTO dto) {
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
                        .orElseThrow(() -> new RuntimeException("PrivacyProperty id=" + pv.getPropertyId() + " not found"));
            } else if (pv.getPropertyName() != null) {
                prop = privacyPropertyService.findByName(pv.getPropertyName())
                        .orElseThrow(() -> new RuntimeException("PrivacyProperty name=" + pv.getPropertyName() + " not found"));
            } else {
                throw new RuntimeException("privacyValue must include propertyId or propertyName");
            }

            DevicePrivacyValue dpv = new DevicePrivacyValue();
            dpv.setDevice(savedDevice);
            dpv.setPrivacyProperty(prop);
            dpv.setValue(pv.getValue());
            devicePrivacyValueService.save(dpv);
        }

        return savedDevice;
    }
}
