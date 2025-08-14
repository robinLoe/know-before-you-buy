package com.kbyb.know_before_you_buy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.repository.DevicePrivacyValueRepository;

@Service
public class DevicePrivacyValueService {

    private final DevicePrivacyValueRepository repository;

    public DevicePrivacyValueService(DevicePrivacyValueRepository repository) {
        this.repository = repository;
    }

    public List<DevicePrivacyValue> findAll() {
        return repository.findAll();
    }

    public List<PrivacyValueDTO> findPrivacyValuesByDeviceId(Integer deviceId) {
        return repository.findPrivacyValuesByDeviceId(deviceId);
    }

    public Optional<DevicePrivacyValue> findById(Integer id) {
        return repository.findById(id);
    }

    public DevicePrivacyValue save(DevicePrivacyValue dpv) {
        return repository.save(dpv);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    // Deletes all privacy value entries for a given device ID.
    public void deletePrivacyValuesForDevice(Integer deviceId) {
        repository.deleteByDeviceId(deviceId);
    }
}