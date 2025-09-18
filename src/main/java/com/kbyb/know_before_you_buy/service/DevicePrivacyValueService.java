package com.kbyb.know_before_you_buy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.DevicePrivacyValueRepository;

// Service class for managing DevicePrivacyValue entities.
@Service
public class DevicePrivacyValueService {

    private final DevicePrivacyValueRepository repository;

    public DevicePrivacyValueService(DevicePrivacyValueRepository repository) {
        this.repository = repository;
    }

    // Retrieves all DevicePrivacyValue entities from the database.
    public List<DevicePrivacyValue> findAll() {
        return repository.findAll();
    }

    // Finds all privacy values for a given device ID, mapped to a DTO.
    public List<PrivacyValueDTO> findPrivacyValuesByDeviceId(Integer deviceId) {
        return repository.findPrivacyValuesByDeviceId(deviceId);
    }

    // Finds all privacy values for a given device name, mapped to a DTO.
    public List<PrivacyValueDTO> findPrivacyValuesByDeviceName(String deviceName) {
        return repository.findPrivacyValuesByDeviceName(deviceName);
    }

    // Finds a single DevicePrivacyValue by its ID.
    public Optional<DevicePrivacyValue> findById(Integer id) {
        return repository.findById(id);
    }

    // Saves a DevicePrivacyValue entity to the database.
    public DevicePrivacyValue save(DevicePrivacyValue dpv) {
        return repository.save(dpv);
    }

    // Retrieves a DevicePrivacyValue by its device ID and privacy property ID, or throws a RuntimeException if not found.
    public DevicePrivacyValue getDpvByDeviceIdAndPropertyId(Integer deviceId, Integer propertyId){
        return repository.getDpvByDeviceIdAndPropertyId(deviceId, propertyId).orElseThrow(() -> new RuntimeException("DevicePrivacyValue not found"));
    }

    // Retrieves a DevicePrivacyValue by its device name and privacy property ID, or throws a RuntimeException if not found.
    public DevicePrivacyValue getDpvByDeviceNameAndPropertyId(String deviceName, Integer propertyId){
        return repository.getDpvByDeviceNameAndPropertyId(deviceName, propertyId).orElseThrow(() -> new RuntimeException("DevicePrivacyValue not found"));
    }

    // Validates and updates the value of a DevicePrivacyValue.
    public DevicePrivacyValue validateAndUpdateDpvValue(String value, DevicePrivacyValue dpv){
        PrivacyProperty prop = dpv.getPrivacyProperty();
        // Validation for allowed values
        if (prop.isValidatable()) {
            String[] allowed = prop.getAllowedValues().split(",");
            String trimmedValue = value.trim();

            if (trimmedValue.equalsIgnoreCase(allowed[0].trim())) {
                dpv.setValue(allowed[0].trim());
            } else if (allowed.length > 1 && trimmedValue.equalsIgnoreCase(allowed[1].trim())) {
                dpv.setValue(allowed[1].trim());
            } else {
                throw new IllegalArgumentException(
                    "Invalid value for property: " + prop.getName()
                    + ".Has to be one of these: " + prop.getAllowedValues()
                    + " but was: " + value
                );
            }
        } else {
            // No validation needed
            dpv.setValue(value);
        }
        return dpv;
    }

    // Deletes a DevicePrivacyValue entry by its ID.
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    // Deletes all privacy value entries for a given device ID.
    public void deletePrivacyValuesByDeviceId(Integer deviceId) {
        repository.deleteByDeviceId(deviceId);
    }

    // Deletes all privacy value entries for a given deviceName.
    public void deletePrivacyValuesByDeviceName(String deviceName) {
        repository.deleteByDeviceName(deviceName);
    }

    // Deletes all privacy value entries for a given privacyPropertyId.
    public void deletePrivacyValuesByPrivacyPropertyId(Integer privacyPropertyId) {
        repository.deleteByPrivacyPropertyId(privacyPropertyId);
    }

    // Deletes all privacy value entries for a given privacyPropertyName.
    public void deletePrivacyValuesByPrivacyPropertyName(String privacyPropertyName) {
        repository.deleteByPrivacyPropertyName(privacyPropertyName);
    }

    // Deletes privacy value entry for a given privacyPropertyName.
    public void deleteByPrivacyPropertyIdAndDeviceId(Integer deviceId, Integer privacyPropertyId) {
        repository.deleteByPrivacyPropertyIdAndDeviceId(deviceId, privacyPropertyId);
    }
}