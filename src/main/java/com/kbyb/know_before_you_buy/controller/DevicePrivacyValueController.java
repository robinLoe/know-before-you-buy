package com.kbyb.know_before_you_buy.controller;

import java.util.List;

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



@RestController
@RequestMapping("/device-privacy-values")
public class DevicePrivacyValueController {

    private final DevicePrivacyValueService service;

    public DevicePrivacyValueController(DevicePrivacyValueService service) {
        this.service = service;
    }

    @GetMapping
    public List<DevicePrivacyValue> getAll() {
        return service.findAll();
    }

    @GetMapping("/getAllFor/{deviceId}")
    public List<PrivacyValueDTO> getAllPpForDeviceId(@PathVariable Integer deviceId) {
        return service.findPrivacyValuesByDeviceId(deviceId);
    }

    @GetMapping("/{id}")
    public DevicePrivacyValue getById(@PathVariable Integer id) {
        return service.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PostMapping
    public DevicePrivacyValue create(@RequestBody DevicePrivacyValue dpv) {
        return service.save(dpv);
    }

    @PutMapping("/{id}")
    public DevicePrivacyValue update(@PathVariable Integer id, @RequestBody DevicePrivacyValue dpv) {
        dpv.setId(id);
        return service.save(dpv);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}